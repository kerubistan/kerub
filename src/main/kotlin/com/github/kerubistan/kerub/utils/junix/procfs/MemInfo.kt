package com.github.kerubistan.kerub.utils.junix.procfs

import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.strings.substringBetween
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object MemInfo : AbstractProcFs {

	fun total(session: ClientSession): BigInteger =
			getMemInfo(session).substringBetween("MemTotal:", "\n").trim().toSize()

	private fun getMemInfo(session: ClientSession): String =
			session.createSftpClient().use {
				sftp ->
				sftp.read("/proc/meminfo").reader().use {
					reader ->
					reader.readText()
				}
			}
}