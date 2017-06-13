package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.FireWall
import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.host.distros.Distribution.Companion.doWithHostDyn
import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.host.fw.IptablesFireWall
import com.github.K0zka.kerub.host.servicemanager.systemd.SystemdServiceManager
import com.github.K0zka.kerub.model.FsStorageCapability
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.StorageDeviceDynamic
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.model.lom.WakeOnLanInfo
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.join
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.df.DF
import com.github.K0zka.kerub.utils.junix.dmi.DmiDecoder
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import com.github.K0zka.kerub.utils.junix.lspci.LsPci
import com.github.K0zka.kerub.utils.junix.mount.Mount
import com.github.K0zka.kerub.utils.junix.mpstat.MPStat
import com.github.K0zka.kerub.utils.junix.procfs.CpuInfo
import com.github.K0zka.kerub.utils.junix.qemu.QemuImg
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.LvmPv
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.LvmVg
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.PhysicalVolume
import com.github.K0zka.kerub.utils.junix.storagemanager.lvm.VolumeGroup
import com.github.K0zka.kerub.utils.junix.sysfs.Net
import com.github.K0zka.kerub.utils.junix.virt.virsh.Virsh
import com.github.K0zka.kerub.utils.junix.vmstat.VmStat
import com.github.K0zka.kerub.utils.silent
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

abstract class AbstractLinux : Distribution {

	override val operatingSystem = OperatingSystem.Linux

	companion object {
		val logger = getLogger(AbstractLinux::class)
		private val packages = mapOf(
				DF to listOf("coreutils"),
				DmiDecoder to listOf("dmidecode"),
				TgtAdmin to listOf("iscsi-target-utils"),
				LsPci to listOf("pciutils"),
				MPStat to listOf("sysstat"),
				QemuImg to listOf("qemu-img"),
				LvmLv to listOf("lvm2"),
				LvmPv to listOf("lvm2"),
				LvmVg to listOf("lvm2"),
				Virsh to listOf("libvirt"),
				VmStat to listOf("procps"),
				Net to listOf()
		)
		private val nonStorageFilesystems = listOf("proc", "devtmpfs", "tmpfs", "cgroup", "debugfs", "pstore")
	}

	override fun installMonitorPackages(session: ClientSession) {
		//TODO: filter what is already installed, do not install if the list is empty
		val packsNeeded =
				arrayOf(VmStat, MPStat)
						.map { util -> getRequiredPackages(util) }
						.join()
		getPackageManager(session).install(*packsNeeded.toTypedArray())
	}

	override fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao,
			hostHistoryDao: HistoryDao<HostDynamic>) {
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

		LvmVg.monitor(session, {
			volGroups ->
			doWithHostDyn(id, hostDynDao, hostHistoryDao) {
				it.copy(
						storageStatus =
						it.storageStatus.filterNot { lvmVgsById?.contains(it.id) ?: true }
								+ volGroups.map {
							volGroup ->
							val storageDevice = lvmVgsByName?.get(volGroup.name)
							if (storageDevice == null) {
								null
							} else {
								StorageDeviceDynamic(
										id = storageDevice.id,
										freeCapacity = volGroup.freeSize
								)
							}
						}.filterNotNull()
				)
			}
		})
		MPStat.monitor(session, {
			stats ->
			doWithHostDyn(id, hostDynDao, hostHistoryDao) {
				it.copy(
						cpuStats = stats
				)
			}
		})
		//TODO: if mpstat is available, vmstat should only update the memory information
		VmStat.vmstat(session, { event ->
			doWithHostDyn(id, hostDynDao, hostHistoryDao) {
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

	override fun getRequiredPackages(osCommand: OsCommand): List<String> =
			packages[osCommand] ?: listOf()

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
				val pvs = silent { LvmPv.list(session) } ?: listOf<PhysicalVolume>()
				(silent { LvmVg.list(session) } ?: listOf<VolumeGroup>()).map {
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
				listOf<LvmStorageCapability>()
			}

	override fun getTotalMemory(session: ClientSession): BigInteger {
		return session
				.executeOrDie("cat /proc/meminfo | grep  MemTotal")
				.substringAfter("MemTotal:").toSize()
	}

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> {
		//TODO: filter out the ones not connected and not wal-enabled
		val macAdddresses = Net.listDevices(session).map { silent { Net.getMacAddress(session, it) } }.filterNotNull()

		if (macAdddresses.isEmpty()) {
			return listOf()
		} else {
			return listOf(WakeOnLanInfo(macAdddresses))
		}

	}

	override fun detectHostCpuType(session: ClientSession): String {
		val processorType = session.execute("uname -p").trim()
		if (processorType == "unknown") {
			return session.execute("uname -m").trim()
		} else {
			return processorType
		}
	}

	override fun detectHostCpuFlags(session: ClientSession): List<String>
			= CpuInfo.list(session).first().flags

	override fun getHostOs(): OperatingSystem = OperatingSystem.Linux
}