package com.github.K0zka.kerub.utils.junix.vmstat

import java.io.OutputStream

abstract class AbstractVmstatOutputStream : OutputStream() {

	companion object {
		private val someSpaces = Regex.fromLiteral("\\s+")
	}

	private val buff: StringBuilder = StringBuilder(128)

	override fun write(data: Int) {
		if (data == 10) {

			val line = buff.toString().trim()
			buff.setLength(0)
			if (line.startsWith("procs") || line.startsWith("r")) {
				return
			}
			val split = line.split(someSpaces)

			handleInput(split)
		} else {
			buff.append(data.toChar())
		}
	}

	abstract protected fun handleInput(split: List<String>)
}