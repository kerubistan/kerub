package com.github.kerubistan.kerub.utils.junix.df

import com.github.kerubistan.kerub.host.executeOrDie
import com.github.kerubistan.kerub.host.process
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.skip
import org.apache.sshd.client.session.ClientSession
import java.io.OutputStream
import java.math.BigInteger

object
DF : OsCommand {

	private val regex = "\\s+".toRegex()
	private const val separator = "--separator"

	private val multiplier = BigInteger("1024")

	fun df(session: ClientSession): List<FilesystemInfo> =
			parse(session.executeOrDie("df -l -P"))

	internal fun parse(output: String) = output.lines().filterNot { it == "" }.skip().map { row ->
		val fields = row.trim().split(regex)
		FilesystemInfo(
				mountPoint = fields[5],
				free = BigInteger(fields[3]) * multiplier,
				used = BigInteger(fields[2]) * multiplier
		)
	}

	fun monitor(session: ClientSession, interval: Int = 60, callback: (List<FilesystemInfo>) -> Unit) =
			session.process(
					"""sh -c "while true; do df -l -P; echo $separator; sleep $interval; done" """,
					output = DfOutputStream(callback)
			)

	class DfOutputStream(private val callback: (List<FilesystemInfo>) -> Unit) : OutputStream() {
		private val buff = StringBuilder()
		override fun write(data: Int) {
			buff.append(data.toChar())
			if (buff.endsWith(separator)) {
				buff.setLength(buff.length - separator.length)
				callback(parse(buff.toString()))
				buff.setLength(0)
			}
		}
	}

}

