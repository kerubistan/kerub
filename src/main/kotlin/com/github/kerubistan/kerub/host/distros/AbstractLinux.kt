package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.doWithDyn
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.fw.IptablesFireWall
import com.github.kerubistan.kerub.host.servicemanager.systemd.SystemdServiceManager
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.StorageDeviceDynamic
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.df.DF
import com.github.kerubistan.kerub.utils.junix.mount.Mount
import com.github.kerubistan.kerub.utils.junix.mpstat.MPStat
import com.github.kerubistan.kerub.utils.junix.procfs.CpuInfo
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmPv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import com.github.kerubistan.kerub.utils.junix.sysfs.Net
import com.github.kerubistan.kerub.utils.junix.vmstat.VmStat
import com.github.kerubistan.kerub.utils.silent
import com.github.kerubistan.kerub.utils.toSize
import com.github.kerubistan.kerub.utils.update
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

abstract class AbstractLinux : Distribution {

	override val operatingSystem = OperatingSystem.Linux

	companion object {
		private val nonStorageFilesystems = listOf("proc", "devtmpfs", "tmpfs", "cgroup", "debugfs", "pstore")
	}

	override fun installMonitorPackages(session: ClientSession, host : Host) {
		//TODO: filter what is already installed, do not install if the list is empty
		val packsNeeded =
				arrayOf(VmStat, MPStat)
						.map { util -> getRequiredPackages(util, host.capabilities) }
						.join()
		getPackageManager(session).install(*packsNeeded.toTypedArray())
	}

	override fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao) {
		val id = host.id

		val lvmStorageCapabilities = host
				.capabilities
				?.storageCapabilities
				?.filter { it is LvmStorageCapability }
		val lvmVgsById = lvmStorageCapabilities
				?.map { (it as LvmStorageCapability).id to it }
				?.toMap()
		val lvmVgsByName = lvmStorageCapabilities
				?.map { (it as LvmStorageCapability).volumeGroupName to it }
				?.toMap()

		val storageIdToMount = host.capabilities?.storageCapabilities
				?.filter { it is FsStorageCapability }
				?.map { it.id to (it as FsStorageCapability).mountPoint }?.toMap() ?: mapOf()

		val storageMountToId = storageIdToMount.map { it.value to it.key }.toMap()

		DF.monitor(session) {
			mounts ->
			hostDynDao.doWithDyn(id) {
				hostDyn ->
				hostDyn.copy(
						storageStatus = hostDyn.storageStatus.update(
								updateList = mounts,
								upKey = { it.mountPoint },
								selfKey = { storageIdToMount[it.id] ?: "" },
								updateMiss = { it },
								selfMiss = {
									up ->
									val stat = storageMountToId[up.mountPoint]
									stat?.let {
										StorageDeviceDynamic(id = it, freeCapacity = up.free)
									}
								},
								merge = {
									devDyn: StorageDeviceDynamic, fsInfo ->
									devDyn.copy(freeCapacity = fsInfo.free)
								}
						)
				)
			}
		}

		if (LvmVg.available(host.capabilities)) {
			LvmVg.monitor(session) { volGroups ->
				hostDynDao.doWithDyn(id) {
					it.copy(
							storageStatus =
							it.storageStatus.filterNot { lvmVgsById?.contains(it.id) != false }
									+ volGroups.mapNotNull { volGroup ->
								val storageDevice = lvmVgsByName?.get(volGroup.name)
								if (storageDevice == null) {
									null
								} else {
									StorageDeviceDynamic(
											id = storageDevice.id,
											freeCapacity = volGroup.freeSize
									)
								}
							}
					)
				}
			}
		}

		MPStat.monitor(session, {
			stats ->
			hostDynDao.doWithDyn(id) {
				it.copy(
						cpuStats = stats
				)
			}
		})
		//TODO: if mpstat is available, vmstat should only update the memory information
		VmStat.vmstat(session, { event ->
			hostDynDao.doWithDyn(id) {
				val memFree = (event.freeMem
						+ event.cacheMem
						+ event.ioBuffMem)
				it.copy(
						status = HostStatus.Up,
						idleCpu = event.idleCpu,
						systemCpu = event.systemCpu,
						userCpu = event.userCpu,
						memFree = memFree,
						memUsed = host.capabilities?.totalMemory?.minus(memFree),
						memSwapped = event.swapMem
				)
			}
		})
	}

	override fun getRequiredPackages(osCommand: OsCommand, capabilities: HostCapabilities?): List<String> =
			capabilities?.distribution?.let { distro ->
				osCommand.providedBy().firstOrNull { (selector, _) ->
					selector(distro)
				}?.second
			} ?: listOf()

	override fun getFireWall(session: ClientSession): FireWall = IptablesFireWall(session)

	override fun getServiceManager(session: ClientSession): ServiceManager {
		return SystemdServiceManager(session)
	}

	override fun detectStorageCapabilities(
			session: ClientSession,
			osVersion: SoftwarePackage,
			packages: List<SoftwarePackage>): List<StorageCapability> {
		return listLvmVolumes(session, osVersion, packages) + listFilesystems(session)
	}

	internal fun listFilesystems(session: ClientSession): List<FsStorageCapability> =
			joinMountsAndDF(
					DF.df(session),
					Mount.listMounts(session).filterNot { nonStorageFilesystems.contains(it.type) }
			)


	internal fun listLvmVolumes(session: ClientSession,
								osVersion: SoftwarePackage,
								packages: List<SoftwarePackage>): List<LvmStorageCapability> =
			if (LvmLv.available(osVersion, packages)) {
				val pvs = silent { LvmPv.list(session) } ?: listOf()
				(silent { LvmVg.list(session) } ?: listOf()).map {
					vg ->
					LvmStorageCapability(
							volumeGroupName = vg.name,
							size = vg.size,
							physicalVolumes = pvs.filter {
								pv ->
								pv.volumeGroupId == vg.id
							}.map { it.size })
				}

			} else {
				listOf()
			}

	override fun getTotalMemory(session: ClientSession): BigInteger {
		return session
				.executeOrDie("cat /proc/meminfo | grep  MemTotal")
				.substringAfter("MemTotal:").toSize()
	}

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> {
		//TODO: filter out the ones not connected and not wal-enabled
		val macAdddresses = Net.listDevices(session).mapNotNull {
			silent(level = LogLevel.Debug) { Net.getMacAddress(session, it) }
		}

		return if (macAdddresses.isEmpty()) {
			listOf()
		} else {
			listOf(WakeOnLanInfo(macAdddresses))
		}

	}

	override fun detectHostCpuType(session: ClientSession): String {
		val processorType = session.execute("uname -p").trim()
		return if (processorType == "unknown") {
			session.execute("uname -m").trim()
		} else {
			processorType
		}
	}

	override fun detectHostCpuFlags(session: ClientSession): List<String>
			= (if (detectHostCpuType(session) == "x86_64") {
		CpuInfo.list(session)
	} else {
		CpuInfo.listPpc(session)
	}).first().flags

	override fun getHostOs(): OperatingSystem = OperatingSystem.Linux
}