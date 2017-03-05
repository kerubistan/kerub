package com.github.K0zka.kerub.utils.junix.common

import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.SoftwarePackage

/**
 * Interface for operating system commands, see subtypes for examples.
 */
interface OsCommand {
	fun providedBy() = listOf<Pair<(SoftwarePackage) -> Boolean, List<String>>>()
	fun available(osVersion: SoftwarePackage, packages: List<SoftwarePackage>) =
			providedBy().any {
				it.first(osVersion)
			}

	fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities?.distribution != null
					&& hostCapabilities.installedSoftware != null
					&& available(hostCapabilities.distribution, hostCapabilities.installedSoftware)
}