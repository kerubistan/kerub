package com.github.kerubistan.kerub.utils.junix.common

import java.io.OutputStream
import java.lang.StringBuilder

class MonitorOutputStream<T>(
		private val separator : String,
		private val callback: (T) -> Unit,
		private val parser: (String) -> T) : OutputStream() {

	private val buffer = StringBuilder()

	override fun write(data: ByteArray) {
		buffer.append(CharArray(data.size) { data[it].toChar() })
		checkBuffer()
	}

	override fun write(data: ByteArray, offset: Int, length: Int) {
		buffer.append(CharArray(length) { data[offset + it].toChar() })
		checkBuffer()
	}

	override fun write(data: Int) {
		buffer.append(data.toChar())
		checkBuffer()
	}

	private fun checkBuffer() {
		var separatorIndex = buffer.indexOf(separator)
		while (separatorIndex >= 0) {
			val content = buffer.substring(0, separatorIndex)
			buffer.delete(0, separatorIndex + separator.length)
			callback(parser(content))
			separatorIndex = buffer.indexOf(separator)
		}
	}
}