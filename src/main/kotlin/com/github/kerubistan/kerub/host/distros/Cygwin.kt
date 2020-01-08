package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.data.dynamic.doWithDyn
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.host.packman.CygwinPackageManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.utils.asPercentOf
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.procfs.MemInfo
import com.github.kerubistan.kerub.utils.junix.procfs.Stat
import com.github.kerubistan.kerub.utils.junix.uname.UName
import io.github.kerubistan.kroki.time.now
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

class Cygwin : Distribution {
	override fun listBlockDevices(session: ClientSession): List<BlockDevice>
	// TODO : find a way to get block device list from windows - wmic maybe?
			= listOf()

	override fun getVersion(session: ClientSession) =
			Version.fromVersionString(UName.kernelVersion(session).substringBefore("("))

	override fun name(): String =
			"Cygwin"

	override fun handlesVersion(version: Version): Boolean = version.major.toInt() >= 2

	override fun detect(session: ClientSession): Boolean = UName.operatingSystem(session) == "Cygwin"

	override fun getPackageManager(session: ClientSession): PackageManager = CygwinPackageManager(session)

	override fun installMonitorPackages(session: ClientSession, host: Host) {
		// do nothing, cygwin can not install, try to work with what is installed
	}

	override fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao,
			vStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
			controllerConfig: ControllerConfig
	) {
		Stat.cpuLoadMonitorIncremental(session) { cpus ->

			val idle = cpus["cpu"]?.idle ?: 0
			val user = cpus["cpu"]?.user ?: 0
			val system = cpus["cpu"]?.system ?: 0
			val sum = system + idle + user

			hostDynDao.doWithDyn(host.id) { dyn ->
				dyn.copy(
						status = HostStatus.Up,
						idleCpu = idle.asPercentOf(sum).toByte(),
						systemCpu = system.asPercentOf(sum).toByte(),
						userCpu = user.asPercentOf(sum).toByte(),
						lastUpdated = now()
				)
			}
		}
	}

	override fun getRequiredPackages(osCommand: OsCommand, capabilities: HostCapabilities?): List<String> = listOf()

	override fun detectStorageCapabilities(
			session: ClientSession, osVersion: SoftwarePackage, packages: List<SoftwarePackage>
	): List<StorageCapability> = listOf()

	override fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo> = listOf() // TODO

	override fun detectHostCpuType(session: ClientSession): String = UName.processorType(session).toUpperCase()

	override fun getTotalMemory(session: ClientSession): BigInteger =
			MemInfo.total(session)

	override fun getFireWall(session: ClientSession): FireWall {
		TODO()
	}

	override fun getServiceManager(session: ClientSession): ServiceManager {
		TODO()
	}

	override fun getHostOs(): OperatingSystem = operatingSystem

	override val operatingSystem = OperatingSystem.Windows
}
