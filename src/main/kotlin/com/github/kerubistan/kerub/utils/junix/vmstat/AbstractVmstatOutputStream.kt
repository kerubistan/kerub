package com.github.kerubistan.kerub.utils.junix.vmstat

import java.io.OutputStream

abstract class AbstractVmstatOutputStream : OutputStream() {

	private val buff: StringBuilder = StringBuilder(128)

	companion object {
		private val someSpaces = "\\s+".toRegex()
	}

	override fun write(data: Int) {
		if (data == 10) {

			val line = buff.toString().trim()
			buff.clear()
			if (line.startsWith("procs") || line.startsWith("r")) {
				return
			}
			val split = line.split(someSpaces)

			handleInput(split)
		} else {
			buff.append(data.toChar())
		}
	}

	protected abstract fun handleInput(split: List<String>)
}