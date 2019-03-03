package com.github.kerubistan.kerub.utils.junix.compression.lz4

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed
import com.github.kerubistan.kerub.utils.junix.compression.StreamCompressionCommand

object Lz4 : StreamCompressionCommand {

	override val format: CompressionFormat
		get() = CompressionFormat.Lz4

	override fun available(hostCapabilities: HostCapabilities?): Boolean =
			hostCapabilities.anyPackageNamed("liblz4-tool")
}