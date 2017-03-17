package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.FireWall
import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.ServiceManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.StorageCapability
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.lom.PowerManagementInfo
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger
import java.util.UUID

/**
 * Interface to hide the details of some distribution-specific operations.
 */
interface Distribution {

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

	fun installMonitorPackages(session: ClientSession)

	/**
	 * Start monitoring processes
	 */
	fun startMonitorProcesses(session: ClientSession, host: Host, hostDynDao: HostDynamicDao)

	/**
	 * Get the list of packages to be installed for a given utility to work.
	 */
	fun getRequiredPackages(osCommand: OsCommand): List<String>

	fun detectStorageCapabilities(session: ClientSession, osVersion: SoftwarePackage, packages: List<SoftwarePackage>): List<StorageCapability>

	fun detectPowerManagement(session: ClientSession) : List<PowerManagementInfo>

	fun detectHostCpuType(session: ClientSession) : String

	fun detectHostCpuFlags(session: ClientSession) : List<String> = listOf()

	fun getTotalMemory(session: ClientSession) : BigInteger

	fun isUtilityAvailable(osCommand: OsCommand, host: Host): Boolean {
		return getRequiredPackages(osCommand).all {
			pack ->
			host.capabilities?.installedSoftware?.any {
				it.name == pack
			} ?: false
		}
	}

	fun getFireWall(session: ClientSession) : FireWall

	fun getServiceManager(session: ClientSession) : ServiceManager

	fun getHostOs() : OperatingSystem

	fun doWithDyn(id: UUID, hostDynDao: HostDynamicDao, action: (HostDynamic) -> HostDynamic) {
		val hostDyn = hostDynDao[id]
		if (hostDyn == null) {
			val newHostDyn = HostDynamic(
					id = id,
					status = HostStatus.Up
			)
			hostDynDao.add(action(newHostDyn))
		} else {
			hostDynDao.update(action(hostDyn))
		}
	}


}