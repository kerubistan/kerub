package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.ClientSession

/**
 * Interface to hide the details of some distribution-specific operations.
 */
public interface Distribution {

	val operatingSystem : OperatingSystem

	/**
	 * Get the version of the host OS distribution.
	 */
	fun getVersion(session: ClientSession) : Version

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
	/**
	 * Use the distribution's package manager to install a package.
	 */
	fun installPackage(pack: String, session: ClientSession)

	/**
	 * Use the distribution's package manager to uninstall a package.
	 */
	fun uninstallPackage(pack: String, session: ClientSession)

	/**
	 * List installed packages
	 */
	fun listPackages(session: ClientSession): List<SoftwarePackage>

	/**
	 * Start monitoring processes
	 */
	fun startMonitorProcesses(session: ClientSession, host : Host, hostDynDao : HostDynamicDao)

	/**
	 * Get the list of packages to be installed for a given utility to work.
	 */
	fun getRequiredPackages(osCommand : OsCommand) : List<String>
}