package com.github.K0zka.kerub.utils.junix.procfs

import com.github.K0zka.kerub.utils.substringBetween
import com.github.K0zka.kerub.utils.toSize
import org.apache.sshd.client.session.ClientSession
import java.math.BigInteger

object MemInfo {

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