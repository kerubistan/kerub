package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.model.SoftwarePackage

/**
 * Interface for the package manager of an OS installation on a host.
 */
interface PackageManager {

	/**
	 * Use the distribution's package manager to install a package.
	 */
	fun install(pack: String)

	/**
	 * Use the distribution's package manager to uninstall a package.
	 */
	fun remove(pack: String)

	/**
	 * List installed packages
	 */
	fun list(): List<SoftwarePackage>
}