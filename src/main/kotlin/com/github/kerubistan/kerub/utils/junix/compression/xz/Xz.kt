package com.github.kerubistan.kerub.utils.junix.compression.xz

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.utils.junix.common.anyPackageNamed
import com.github.kerubistan.kerub.utils.junix.compression.StreamCompressionCommand

object Xz : StreamCompressionCommand {
	override val format: CompressionFormat
		get() = CompressionFormat.Xz

	override fun available(hostCapabilities: HostCapabilities?) =
			hostCapabilities.anyPackageNamed("xz-utils")
}