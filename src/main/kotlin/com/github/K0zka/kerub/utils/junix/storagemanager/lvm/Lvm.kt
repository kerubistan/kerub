package com.github.K0zka.kerub.utils.junix.storagemanager.lvm

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.common.OsCommand

open class Lvm : OsCommand {
	override fun providedBy(): List<Pair<(SoftwarePackage) -> Boolean, List<String>>> =
			listOf()
}