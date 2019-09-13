package com.github.kerubistan.kerub.utils.junix.du

import com.github.kerubistan.kerub.host.bashMonitor
import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.utils.junix.common.MonitorOutputStream
import com.github.kerubistan.kerub.utils.toBigInteger
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object DU {

	private const val separator = "--end"
	private val spaces = "\\s+".toRegex()

	fun du(session: ClientSession, file: String) =
			session.executeOrDie("du -B 1 $file").substringBefore("\t").toBigInteger()


	fun monitor(session: ClientSession, directory : String, callback : (Map<String, BigInteger>) -> Unit ) {

		fun parseOutput(output : String) = output.lines().filterNot { it.isEmpty() }.map {
			it.split(spaces).let {  columns -> columns[1].removePrefix(directory) to columns.first().toBigInteger() }
		}.toMap()

		session.bashMonitor(
				command = "du -B 1 $directory/*", interval = 10, separator = separator,
				output = MonitorOutputStream(separator = separator, parser = ::parseOutput, callback = callback)
		)
	}

}