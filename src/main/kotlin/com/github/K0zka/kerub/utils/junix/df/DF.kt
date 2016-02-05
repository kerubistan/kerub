package com.github.K0zka.kerub.utils.junix.df

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import org.apache.sshd.ClientSession
import java.math.BigInteger

object DF : OsCommand {

	private val regex = "\\s+".toRegex()

	val multiplier = BigInteger("1024")
	fun df(session : ClientSession) : List<FilesystemInfo> {
		val output = session.executeOrDie("df -l -P").split("\n").filterNot { it == "" }
		return (output - output.first()).map {
			row ->
			val fields = row.trim().split(regex)
			FilesystemInfo(
					mountPoint = fields[5],
					free = BigInteger(fields[3]) * multiplier,
					used = BigInteger(fields[2]) * multiplier
			)
		}
	}
}