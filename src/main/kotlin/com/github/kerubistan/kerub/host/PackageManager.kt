package com.github.kerubistan.kerub.host

import com.github.kerubistan.kerub.model.SoftwarePackage

/**
 * Interface for the package manager of an OS installation on a host.
 */
interface PackageManager {

	/**
	 * Use the distribution's package manager to install packages.
	 */
	fun install(vararg pack: String)

	/**
	 * Use the distribution's package manager to uninstall packages.
	 */
	fun remove(vararg pack: String)

	/**
	 * List installed packages
	 */
	fun list(): List<SoftwarePackage>
}