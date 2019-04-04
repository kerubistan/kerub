package com.github.kerubistan.kerub.utils.junix.iostat

import java.io.OutputStream

class IOStatOutputStream(private val listener: (List<IOStatEvent>) -> Unit) : OutputStream() {

	companion object {
		private val fieldSplitter = "\\s+".toRegex()
		private val headerMatcher = "^Device.*kB_wrtn$".toRegex()
		private val startHeaderMatcher = ".*CPU\\)\n\n$".toRegex()
	}

	private val buffer = StringBuilder()

	override fun write(data: Int) {
		buffer.append(data.toChar())
		if (buffer.endsWith("\n\n")) {
			if (!buffer.matches(startHeaderMatcher)) {
				listener(
						buffer.lines().filter { it.isNotBlank() && !it.matches(headerMatcher) }.map { line ->
							val fields = line.split(fieldSplitter)
							IOStatEvent(
									diskDevice = fields[0],
									readKb = fields[4].toInt(),
									readKbPerSec = fields[2].toFloat(),
									writeKb = fields[5].toInt(),
									writeKbPerSec = fields[3].toFloat(),
									tps = fields[1].toFloat()
							)
						}
				)
			}
			buffer.clear()
		}
	}

}
