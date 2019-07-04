package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
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
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.StorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.network.BondInterface
import com.github.kerubistan.kerub.network.EthernetPort
import com.github.kerubistan.kerub.network.NetworkInterface
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.df.DF
import com.github.kerubistan.kerub.utils.junix.lsblk.Lsblk
import com.github.kerubistan.kerub.utils.junix.lshw.HardwareItem
import com.github.kerubistan.kerub.utils.junix.lshw.Lshw
import com.github.kerubistan.kerub.utils.junix.mount.Mount
import com.github.kerubistan.kerub.utils.junix.mpstat.MPStat
import com.github.kerubistan.kerub.utils.junix.procfs.Bonding
import com.github.kerubistan.kerub.utils.junix.procfs.CpuInfo
import com.github.kerubistan.kerub.utils.junix.procfs.CpuInfoRecord
import com.github.kerubistan.kerub.utils.junix.sensors.CpuTemperatureInfo
import com.github.kerubistan.kerub.utils.junix.sensors.Sensors
import com.github.kerubistan.kerub.utils.junix.smartmontools.SmartCtl
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmPv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import com.github.kerubistan.kerub.utils.junix.sysfs.Net
import com.github.kerubistan.kerub.utils.junix.vmstat.VmStat
import com.github.kerubistan.kerub.utils.silent
import com.github.kerubistan.kerub.utils.toSize
import com.github.kerubistan.kerub.utils.update
import io.github.kerubistan.kroki.collections.join
import io.github.kerubistan.kroki.objects.find
import io.github.kerubistan.kroki.strings.isUUID
import io.github.kerubistan.kroki.strings.toUUID
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger
import com.github.kerubistan.kerub.utils.junix.lshw.NetworkInterface as LshwNetworkInterface


abstract class AbstractLinux : Distribution {

	override fun listBlockDevices(session: ClientSession): List<BlockDevice> =
			Lsblk.list(session, noDeps = true).map { BlockDevice(deviceName = it.name, storageCapacity = it.size) }

	override val operatingSystem = OperatingSystem.Linux

	companion object {
		private val nonStorageFilesystems = listOf("proc", "devtmpfs", "tmpfs", "cgroup", "debugfs", "pstore")
	}

	override fun installMonitorPackages(session: ClientSession, host: Host) {
		//TODO: filter what is already installed, do not install if the list is empty
		val packsNeeded =
				arrayOf(VmStat, MPStat)
						.map { util -> getRequiredPackages(util, host.capabilities) }
						.join()
		if (packsNeeded.isNotEmpty()) {
			getPackageManager(session).install(*packsNeeded.toTypedArray())
		}
	}

	override fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao
	) {
		startFsMonitoring(host, session, hostDynDao)

		startLvmMonitoring(host, session, hostDynDao, vStorageDeviceDynamicDao)

		startHwHealthMonitoring(host, session, hostDynDao)

		startCpuUsageMonitoring(session, hostDynDao, host)
	}

	private fun startCpuUsageMonitoring(
			session: ClientSession,
			hostDynDao: HostDynamicDao,
			host: Host
	) {
		MPStat.monitor(session, { stats ->
			hostDynDao.doWithDyn(host.id) {
				it.copy(
						cpuStats = stats
				)
			}
		})
		//TODO: if mpstat is available, vmstat should only update the memory information
		VmStat.vmstat(session, { event ->
			hostDynDao.doWithDyn(host.id) {
				val memFree = (event.freeMem
						+ event.cacheMem
						+ event.ioBuffMem)
				it.copy(
						status = HostStatus.Up,
						idleCpu = event.idleCpu,
						systemCpu = event.systemCpu,
						userCpu = event.userCpu,
						memFree = memFree,
						memUsed = (host.capabilities?.totalMemory?.minus(memFree))?.coerceAtLeast(BigInteger.ZERO),
						memSwapped = event.swapMem
				)
			}
		})
	}

	private fun startHwHealthMonitoring(
			host: Host, session: ClientSession,
			hostDynDao: HostDynamicDao
	) {
		if (Sensors.available(host.capabilities)) {
			Sensors.monitorCpuTemperatures(session) { temperatures ->
				hostDynDao.doWithDyn(host.id) {
					it.copy(
							cpuTemperature = temperatures.map(CpuTemperatureInfo::temperature)
					)
				}
			}
		}

		if (SmartCtl.available(host.capabilities)) {
			host.capabilities?.blockDevices?.forEach { storageDevice ->
				SmartCtl.monitor(session, device = "/dev/${storageDevice.deviceName}") { healthy ->
					hostDynDao.doWithDyn(host.id) {
						it.copy(
								storageDeviceHealth = it.storageDeviceHealth + (storageDevice.deviceName to healthy)
						)
					}
				}
			}
		}
	}

	private fun startFsMonitoring(
			host: Host,
			session: ClientSession,
			hostDynDao: HostDynamicDao
	) {
		val storageIdToMount = host.capabilities?.storageCapabilities
				?.filter { it is FsStorageCapability }
				?.map { it.id to (it as FsStorageCapability).mountPoint }?.toMap() ?: mapOf()

		val storageMountToId = storageIdToMount.map { it.value to it.key }.toMap()

		DF.monitor(session) { mounts ->
			hostDynDao.doWithDyn(host.id) { hostDyn ->
				hostDyn.copy(
						storageStatus = hostDyn.storageStatus.update(
								updateList = mounts,
								upKey = { it.mountPoint },
								selfKey = { storageIdToMount[it.id] ?: "" },
								updateMiss = { it },
								selfMiss = { up ->
									val stat = storageMountToId[up.mountPoint]
									stat?.let {
										SimpleStorageDeviceDynamic(id = it, freeCapacity = up.free)
									}
								},
								merge = { devDyn: StorageDeviceDynamic, fsInfo ->
									(devDyn as SimpleStorageDeviceDynamic).copy(freeCapacity = fsInfo.free)
								}
						)
				)
			}
		}
	}

	private fun startLvmMonitoring(
			host: Host, session: ClientSession,
			hostDynDao: HostDynamicDao,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao
	) {

		val lvmStorageCapabilities = host
				.capabilities
				?.storageCapabilities
				?.filterIsInstance<LvmStorageCapability>()
		val lvmVgsById = lvmStorageCapabilities?.associateBy { it.id }
		val lvmVgsByName = lvmStorageCapabilities?.associateBy { it.volumeGroupName }

		if (LvmVg.available(host.capabilities)) {
			LvmVg.monitor(session) { volGroups ->
				hostDynDao.doWithDyn(host.id) {
					it.copy(
							storageStatus =
							it.storageStatus.filterNot { lvmVgsById?.contains(it.id) != false }
									+ volGroups.mapNotNull { volGroup ->
								val storageDevice = lvmVgsByName?.get(volGroup.name)
								if (storageDevice == null) {
									null
								} else {
									SimpleStorageDeviceDynamic(
											id = storageDevice.id,
											freeCapacity = volGroup.freeSize
									)
								}
							}
					)
				}
			}
		}

		if (LvmLv.available(host.capabilities)) {
			LvmLv.monitor(session) { volumes ->
				volumes.filter { it.name.isUUID() }.forEach { volume ->
					vStorageDeviceDynamicDao.update(volume.name.toUUID()) { oldDyn ->
						oldDyn.copy(
								allocations = oldDyn.allocations.map { allocation ->
									if (allocation is VirtualStorageLvmAllocation && allocation.hostId == host.id) {
										allocation.copy(
												actualSize = volume.size
										)
									} else allocation

								}
						)
					}
				}
			}
		}

		if (LvmVg.available(host.capabilities)) {
			LvmVg.monitor(session) { volGroups ->
				hostDynDao.update(host.id) { hostDyn ->
					volGroups.forEach { volGroup ->
						println("${volGroup.name}		${volGroup.freeSize}")
					}
					val lvmGroupsByName = volGroups.associateBy { it.name }
					hostDyn.copy(
							storageStatus = hostDyn.storageStatus.map { status ->
								//TODO
								status
							}
					)
				}
			}
		}
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
			packages: List<SoftwarePackage>
	): List<StorageCapability> {
		return listLvmVolumes(session, osVersion, packages) + listFilesystems(session)
	}

	private fun listFilesystems(session: ClientSession): List<FsStorageCapability> =
			joinMountsAndDF(
					DF.df(session),
					Mount.listMounts(session).filterNot { nonStorageFilesystems.contains(it.type) }
			)


	private fun listLvmVolumes(
			session: ClientSession,
			osVersion: SoftwarePackage,
			packages: List<SoftwarePackage>
	): List<LvmStorageCapability> =
			if (LvmLv.available(osVersion, packages)) {
				val pvs = silent { LvmPv.list(session) } ?: listOf()
				(silent { LvmVg.list(session) } ?: listOf()).map { vg ->
					LvmStorageCapability(
							volumeGroupName = vg.name,
							size = vg.size,
							physicalVolumes = pvs.filter { pv ->
								pv.volumeGroupId == vg.id
							}.associate { it.device to it.size })
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

	override fun detectHostCpuFlags(session: ClientSession): List<String> {
		val cpuInfoRecordFlags: (CpuInfoRecord) -> List<String> = { it.flags }
		return when (detectHostCpuType(session)) {
			"aarch64" -> CpuInfo.listArm(session).map { it.flags }
			"x86_64" -> CpuInfo.list(session).map(cpuInfoRecordFlags)
			else -> CpuInfo.listPpc(session).map(cpuInfoRecordFlags)
		}.join().toSet().toList()
	} // which isn't true


	override fun getHostOs(): OperatingSystem = OperatingSystem.Linux

	override fun detectHostCapabilities(capabilities: HostCapabilities, session: ClientSession): HostCapabilities =
			super.detectHostCapabilities(capabilities, session).copy(
					networkInterfaces = detectNetworkInterfaces(capabilities, session),
					networkPorts = detectNetworkPorts(capabilities, session)
			)

	private fun detectNetworkPorts(capabilities: HostCapabilities, session: ClientSession): List<EthernetPort> =
			if (Lshw.available(capabilities)) {
				Lshw.list(session).find<HardwareItem>(
						selector = { it.children ?: listOf() }
				).filterIsInstance<LshwNetworkInterface>().filter { it.configuration?.get("driver") != "bonding" }.map {
					EthernetPort(
							device = it.logicalName?: "",
							portSpeed = it.capacity?.toLong() ?: 0,
							switchId = null
					)
				}
			} else listOf()

	private fun detectNetworkInterfaces(capabilities: HostCapabilities, session: ClientSession): List<NetworkInterface> =
			listInterfaces(capabilities, session) + (silent { listBonds(capabilities, session) } ?: listOf())

	private fun listInterfaces(capabilities: HostCapabilities, session: ClientSession): List<NetworkInterface> =
			listOf()

	private fun listBonds(capabilities: HostCapabilities, session: ClientSession): List<NetworkInterface> =
			if (Bonding.available(capabilities)) {
				Bonding.listBondInterfaces(session).map { it to Bonding.getBondInfo(session, it) }.map {
					(name, bondInfo) ->
					BondInterface(
							name = name,
							devices = bondInfo.slaves.map { it.name },
							portSpeedPerSec = bondInfo.slaves.sumBy { (it.speedMbps ?: 0) } /* * MB*/
					)
				}
			} else listOf()

}