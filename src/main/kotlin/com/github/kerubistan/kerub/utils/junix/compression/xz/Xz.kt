package com.github.kerubistan.kerub.utils.junix.compression.xz

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed

object Xz : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities.anyPackageNamed("xz-utils")
}