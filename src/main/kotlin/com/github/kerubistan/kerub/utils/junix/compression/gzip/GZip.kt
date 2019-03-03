package com.github.kerubistan.kerub.utils.junix.compression.gzip

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed
import com.github.kerubistan.kerub.utils.junix.compression.StreamCompressionCommand

object GZip : StreamCompressionCommand {

	override val format: CompressionFormat
		get() = CompressionFormat.Gzip

	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities.anyPackageNamed("gzip")
}