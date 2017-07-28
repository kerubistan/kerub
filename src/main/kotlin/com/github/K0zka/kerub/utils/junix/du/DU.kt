package com.github.K0zka.kerub.utils.junix.du

import com.github.K0zka.kerub.host.executeOrDie
import com.github.K0zka.kerub.utils.toBigInteger
import org.apache.sshd.client.session.ClientSession

object DU {
	fun du(session: ClientSession, file: String) =
			session.executeOrDie("du -B 1 $file").substringBefore("\t").toBigInteger()
}