package com.github.kerubistan.kerub.utils.junix.common

import java.io.OutputStream
import java.lang.StringBuilder

class MonitorOutputStream<T>(
		private val separator : String,
		private val callback: (T) -> Unit,
		private val parser: (String) -> T) : OutputStream() {

	private val buffer = StringBuilder()

	override fun write(data: Int) {
		buffer.append(data.toChar())
		if(buffer.endsWith(separator)) {
			callback(parser(buffer.removeSuffix(separator).toString()))
			buffer.clear()
		}
	}
}