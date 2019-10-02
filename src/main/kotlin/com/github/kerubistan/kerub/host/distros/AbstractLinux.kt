package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.data.dynamic.doWithDyn
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.ServiceManager
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
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamicItem
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.StorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.StoragePoolDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.network.BondInterface
import com.github.kerubistan.kerub.network.EthernetPort
import com.github.kerubistan.kerub.network.NetworkInterface
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.df.DF
import com.github.kerubistan.kerub.utils.junix.du.DU
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
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmLv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmPv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.PhysicalVolume
import com.github.kerubistan.kerub.utils.junix.sysfs.Net
import com.github.kerubistan.kerub.utils.junix.uname.UName
import com.github.kerubistan.kerub.utils.junix.vmstat.VmStat
import com.github.kerubistan.kerub.utils.mergeInstancesWith
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
		private val logger = getLogger(AbstractLinux::class)
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
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
			controllerConfig: ControllerConfig
	) {
		startFsMonitoring(host, session, hostDynDao, vStorageDeviceDynamicDao, controllerConfig)

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

	internal fun startFsMonitoring(
			host: Host,
			session: ClientSession,
			hostDynDao: HostDynamicDao,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
			controllerConfig: ControllerConfig
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

		host.capabilities?.storageCapabilities
				?.filterIsInstance<FsStorageCapability>()
				?.filter { fsCapability ->
					controllerConfig.storageTechnologies.fsPathEnabled.any {
						enabledPath -> fsCapability.mountPoint.startsWith(enabledPath)
					}
				}
				?.forEach { fsCapability ->
			DU.monitor(session, fsCapability.mountPoint) {
				fileSizes ->
				fun String.virtualDiskName() = this.substringAfterLast("/").substringBeforeLast(".")
				fileSizes.entries.filter { it.key.virtualDiskName().isUUID() }.forEach {
					(fileName, fileSize) ->
					val vDiskId = fileName.virtualDiskName().toUUID()
					val type = fileName.substringAfterLast(".", "raw")
					vStorageDeviceDynamicDao.update(
							vDiskId,
							retrieve = {
								vStorageDeviceDynamicDao[it]
									?: VirtualStorageDeviceDynamic(id = vDiskId, allocations = listOf())
							},
							change = {
								dyn ->
								dyn.copy(
										allocations = dyn.allocations.update(
												selector = {
													it is VirtualStorageFsAllocation
															&& it.hostId == host.id
															&& it.capabilityId == fsCapability.id
												},
												default = {
													VirtualStorageFsAllocation(
															hostId = host.id,
															actualSize = fileSize,
															type = silent { VirtualDiskFormat.valueOf(type) }
																	?: VirtualDiskFormat.raw,
															capabilityId = fsCapability.id,
															fileName = fileName,
															mountPoint = fsCapability.mountPoint
													)
												},
												map = {
													(it as VirtualStorageFsAllocation).copy(
															actualSize = fileSize
													)
												}
										)
								)
							}
					)
				}
			}
		}

	}

	private fun startLvmMonitoring(
			host: Host, session: ClientSession,
			hostDynDao: HostDynamicDao,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao
	) {

		if (LvmLv.available(host.capabilities)) {
			startLvmVgMonitoring(host, session, hostDynDao)
			startLvmLvMonitoring(host, session, vStorageDeviceDynamicDao, hostDynDao)
		}
	}

	fun LogicalVolume.freeSize(): BigInteger = (this.size.toBigDecimal()
			* ((this.dataPercent
			?: 0.toDouble()) / 100).toBigDecimal())
			.toBigInteger()

	internal fun startLvmLvMonitoring(
			host: Host,
			session: ClientSession,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
			hostDynDao: HostDynamicDao
	) {
		LvmLv.monitor(session) { volumes ->
			val pools = volumes.filter { it.layout.contains("pool") }
			if(pools.isNotEmpty()) {
				val poolsByVolumeGroup = pools.groupBy { it.volumeGroupName }
				hostDynDao.update(
						host.id,
						retrieve = { hostDynDao[host.id] ?: HostDynamic(id = host.id, status = HostStatus.Up) } ) { dyn ->
					dyn.copy(
							storageStatus = dyn.storageStatus.mergeInstancesWith(
									leftItems = poolsByVolumeGroup.entries,
									merge = {
										devDyn : CompositeStorageDeviceDynamic,
										(_, volumes: List<LogicalVolume>) ->
										devDyn.copy(
												pools = volumes.map {
													poolVolume ->
													StoragePoolDynamic(
															name = poolVolume.name,
															size = poolVolume.size,
															freeSize = poolVolume.freeSize()
													)
												}
										)
									},
									rightValue = CompositeStorageDeviceDynamic::id,
									leftValue = {
										(volumeGroupName, _) ->
										host.capabilities?.index?.lvmStorageCapabilitiesByVolumeGroupName
												?.get(volumeGroupName)?.id
									},
									miss = { it }
							)
					)
				}
			}
			volumes.filter { it.name.isUUID() }.forEach { volume ->
				vStorageDeviceDynamicDao.updateIfExists(volume.name.toUUID()) { oldDyn ->
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

	internal fun startLvmVgMonitoring(
			host: Host, session: ClientSession,
			hostDynDao: HostDynamicDao
	) {
		val lvmStorageCapabilities = host
				.capabilities
				?.storageCapabilities
				?.filterIsInstance<LvmStorageCapability>()
		val lvmVgsById = lvmStorageCapabilities?.associateBy { it.id } ?: mapOf()
		val lvmCapsByName = lvmStorageCapabilities?.associateBy { it.volumeGroupName }

		LvmVg.monitor(session) { volGroups ->
			hostDynDao.update(host.id) {
				logger.debug( "updating {} vgs", host.id)
				it.copy(
						storageStatus =
								it.storageStatus.mergeInstancesWith(
										leftItems = volGroups,
										rightValue = StorageDeviceDynamic::id,
										leftValue = { volumeGroup -> lvmCapsByName?.get(volumeGroup.name)?.id },
										merge = {
											storageDeviceDynamic : CompositeStorageDeviceDynamic, volumeGroup ->
											storageDeviceDynamic.copy(
												reportedFreeCapacity = volumeGroup.freeSize
											)
										},
										missLeft = {
											volGroup ->
											if(lvmCapsByName != null) {
												logger.debug("vol group new: {}", volGroup.name)
												lvmCapsByName[volGroup.name]?.let {
													lvmCap ->
													CompositeStorageDeviceDynamic(
															id = lvmCap.id,
															reportedFreeCapacity = volGroup.freeSize,
															items = listOf() // lvm pvs monitor should fill it
													)
												}
											} else null
										})
				)
			}
		}
		LvmPv.monitor(session) {
			physicalVolumes ->
			val physicalVolumesByVg: Map<String, List<PhysicalVolume>> = physicalVolumes.groupBy(PhysicalVolume::volumeGroupName)
			hostDynDao.update(host.id) { hostDynamic ->
				hostDynamic.copy(
						storageStatus = hostDynamic.storageStatus.mergeInstancesWith(
								rightValue = { dynamic -> lvmVgsById[dynamic.id]?.volumeGroupName },
								leftItems = physicalVolumesByVg.entries,
								leftValue = Map.Entry<String, List<PhysicalVolume>>::key,
								miss = { it },
								merge = { dyn: CompositeStorageDeviceDynamic, updates ->
									dyn.copy(
											items = updates.value.map { volume ->
												CompositeStorageDeviceDynamicItem(
														name = volume.device,
														freeCapacity = volume.freeSize
												)
											}
									)
								}
						)
				)
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
		val processorType = UName.processorType(session)
		return if (processorType == "unknown") {
			UName.machineType(session)
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