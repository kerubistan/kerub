package com.github.kerubistan.kerub.utils.junix.storagemanager.lvm

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.common.OsCommand

open class Lvm : OsCommand {
	override fun available(osVersion: SoftwarePackage, packages: List<SoftwarePackage>): Boolean =
			packages.any { it.name == "lvm2" }
}