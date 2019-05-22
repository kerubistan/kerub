package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.data.dynamic.doWithDyn
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.host.execute
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.fw.IpfwFireWall
import com.github.kerubistan.kerub.host.packman.PkgPackageManager
import com.github.kerubistan.kerub.host.servicemanager.rc.RcServiceManager
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.dmesg.BsdDmesg
import com.github.kerubistan.kerub.utils.junix.geom.Geom
import com.github.kerubistan.kerub.utils.junix.ifconfig.IfConfig
import com.github.kerubistan.kerub.utils.junix.storagemanager.gvinum.GVinum
import com.github.kerubistan.kerub.utils.junix.sysctl.BsdSysCtl
import com.github.kerubistan.kerub.utils.junix.vmstat.BsdVmStat
import com.github.kerubistan.kerub.utils.stringToMac
import com.github.kerubistan.kerub.utils.toBigInteger
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

/**
 * FreeBSD distribution.
 * Some settings need to be modified in sshd configuration:
 * PermitRootLogin yes
 */
class FreeBSD : Distribution {

	override fun listBlockDevices(session: ClientSession): List<BlockDevice> =
			Geom.list(session).map { BlockDevice(deviceName = it.name, storageCapacity = it.mediaSize) }

	override fun getTotalMemory(session: ClientSession): BigInteger {
		return (session
				.executeOrDie("sysctl hw.physmem")
				.substringAfter("hw.physmem:").trim())
				.toBigInteger()
	}

	override fun detectStorageCapabilities(session: ClientSession, osVersion: SoftwarePackage, packages: List<SoftwarePackage>): List<StorageCapability> {
		return GVinum.listDrives(session).let { gvinumDrives ->
			if(gvinumDrives.isEmpty()) {
				listOf()
			} else {
				listOf(
						GvinumStorageCapability(
								devices = gvinumDrives.map {
									GvinumStorageCapabilityDrive(name = it.name, size = it.size, device = it.device)
								}
						)
				)

			}
		}
	}

	override fun getServiceManager(session: ClientSession): ServiceManager {
		return RcServiceManager(session)
	}

	override fun installMonitorPackages(session: ClientSession, host : Host) {
		//TODO issue #57
	}

	override fun getRequiredPackages(osCommand: OsCommand, capabilities: HostCapabilities?): List<String> {
		//TODO issue #57
		return listOf()
	}

	override val operatingSystem = OperatingSystem.BSD

	override fun getVersion(session: ClientSession): Version = Version.fromVersionString(session.execute("uname -r"))

	override fun name(): String = "FreeBSD"

	override fun handlesVersion(version: Version): Boolean = version.major >= "10"

	override fun detect(session: ClientSession): Boolean = session.executeOrDie("uname -s").trim() == "FreeBSD"

	override fun getPackageManager(session: ClientSession) = PkgPackageManager(session)

	override fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao
	) {
		BsdVmStat.vmstat(session, {
			event ->
			val dyn = hostDynDao[host.id] ?: HostDynamic(
					id = host.id,
					status = HostStatus.Up
			)

			hostDynDao.update(dyn.copy(
					memFree = event.freeMem,
					systemCpu = event.systemCpu,
					userCpu = event.userCpu,
					idleCpu = event.idleCpu
			))
		})
		GVinum.monitorDrives(session) {
			disks ->
			val gvinumCapabilities = host.capabilities
					?.storageCapabilities
					?.filter { it is GvinumStorageCapability }
					?: listOf()
			val gvinumDiskIds = gvinumCapabilities.map { it.id }
			hostDynDao.doWithDyn(host.id) {
				it.copy(
						storageStatus = it
								.storageStatus
								.filterNot { storageStat -> gvinumDiskIds.contains(storageStat.id) }
								+ disks.map {
							disk ->
							val cap = gvinumCapabilities.filterIsInstance<GvinumStorageCapability>().single()
							SimpleStorageDeviceDynamic(id = cap.id, freeCapacity = disk.available)
						}
				)
			}
		}
		BsdSysCtl.monitorCpuTemperatures(session) {
			temperatures ->
			hostDynDao.doWithDyn(host.id) {
				hostDynamic ->
				hostDynamic.copy(
						cpuTemperature = temperatures.sortedBy { it.first }.map { it.second }
				)
			}
		}
	}

	override fun getFireWall(session: ClientSession): FireWall = IpfwFireWall(session)

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> {
		val macs = IfConfig.list(session).mapNotNull { it.mac }.map { stringToMac(it) }
		return if (macs.isEmpty()) listOf() else listOf(WakeOnLanInfo(macs))
	}

	//BSD distributions have different naming conventions for architectures
	private val cpuTypeMap = mapOf(
			"amd64" to "X86_64"
	)

	override fun detectHostCpuType(session: ClientSession): String {
		val cpuType = cpuTypeByOS(session)
		return cpuTypeMap[cpuType] ?: cpuType
	}

	override fun detectHostCpuFlags(session: ClientSession): List<String>
			= BsdDmesg.listCpuFlags(session)

	private fun cpuTypeByOS(session: ClientSession): String {
		val processorType = session.execute("uname -p").trim()
		return if (processorType == "unknown") {
			session.execute("uname -m").trim()
		} else {
			processorType
		}
	}

	override fun getHostOs(): OperatingSystem = OperatingSystem.BSD

}