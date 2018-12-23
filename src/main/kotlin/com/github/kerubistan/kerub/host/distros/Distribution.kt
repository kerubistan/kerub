package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.FireWall
import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.ServiceManager
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.controller.config.StorageTechnologiesConfig
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.lom.PowerManagementInfo
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.junix.benchmarks.bonnie.Bonnie
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

/**
 * Interface to hide the details of some distribution-specific operations.
 */
interface Distribution {

	companion object {
		val logger = getLogger(Distribution::class)
	}

	val operatingSystem: OperatingSystem

	/**
	 * Get the version of the host OS distribution.
	 */
	fun getVersion(session: ClientSession): Version

	/**
	 * Get the name of the dstribution handler
	 */
	fun name(): String

	/**
	 * Check if the distribution handler supports a given release of the OS.
	 */
	fun handlesVersion(version: Version): Boolean

	/**
	 * Try to detect if the implementation handles the connected host OS' distribution.
	 *
	 * See list of Linux distributions:
	 * https://www.novell.com/coolsolutions/feature/11251.html
	 */
	fun detect(session: ClientSession): Boolean

	fun getPackageManager(session: ClientSession): PackageManager

	fun installMonitorPackages(session: ClientSession, host : Host)

	/**
	 * Start monitoring processes
	 */
	fun startMonitorProcesses(
			session: ClientSession,
			host: Host,
			hostDynDao: HostDynamicDao)

	/**
	 * Get the list of packages to be installed for a given utility to work.
	 */
	fun getRequiredPackages(osCommand: OsCommand, capabilities: HostCapabilities?): List<String>

	fun detectStorageCapabilities(session: ClientSession,
								  osVersion: SoftwarePackage,
								  packages: List<SoftwarePackage>): List<StorageCapability>

	fun detectPowerManagement(session: ClientSession): List<PowerManagementInfo>

	fun detectHostCpuType(session: ClientSession): String

	fun detectHostCpuFlags(session: ClientSession): List<String> = listOf()

	fun getTotalMemory(session: ClientSession): BigInteger

	fun isUtilityAvailable(osCommand: OsCommand, host: Host): Boolean {
		return getRequiredPackages(osCommand, host.capabilities).all { pack ->
			host.capabilities?.installedSoftware?.any {
				it.name == pack
			} ?: false
		}
	}

	fun getFireWall(session: ClientSession): FireWall

	fun getServiceManager(session: ClientSession): ServiceManager

	fun getHostOs(): OperatingSystem

	fun storageBenchmark(session: ClientSession,
						 capability: StorageCapability,
						 osVersion: SoftwarePackage,
						 packages: List<SoftwarePackage>,
						 storageTechnologies: StorageTechnologiesConfig): StorageCapability =
			if (capability is FsStorageCapability
					&& storageTechnologies.fsPathEnabled.contains(capability.mountPoint)
					&& storageTechnologies.fsTypeEnabled.contains(capability.fsType)) {
				if (Bonnie.available(osVersion, packages)) {
					logger.info("benchmarking storage {}", capability)
					capability.copy(
							performanceInfo = silent {
								Bonnie.run(session = session, directory = capability.mountPoint)
							}
					)
				} else capability
			} else capability

	fun listBlockDevices(session: ClientSession): List<BlockDevice>

}