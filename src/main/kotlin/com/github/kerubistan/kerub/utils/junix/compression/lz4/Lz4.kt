package com.github.kerubistan.kerub.utils.junix.compression.lz4

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed

object Lz4 : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?): Boolean =
			hostCapabilities.anyPackageNamed("liblz4-tool")
}