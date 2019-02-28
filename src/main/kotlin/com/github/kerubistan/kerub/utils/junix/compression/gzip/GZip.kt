package com.github.kerubistan.kerub.utils.junix.compression.gzip

import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed

object GZip : OsCommand {
	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities.anyPackageNamed("gzip")
}