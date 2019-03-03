package com.github.kerubistan.kerub.utils.junix.compression.bzip2

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed
import com.github.kerubistan.kerub.utils.junix.compression.StreamCompressionCommand

object BZip2 : StreamCompressionCommand {

	override fun available(hostCapabilities: HostCapabilities?) = hostCapabilities.anyPackageNamed("bzip2")

	override val format: CompressionFormat
		get() = CompressionFormat.Bzip2
}