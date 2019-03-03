package com.github.kerubistan.kerub.utils.junix.compression

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.utils.junix.common.OsCommand

interface StreamCompressionCommand : OsCommand {
	val format: CompressionFormat
}