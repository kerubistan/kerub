package com.github.K0zka.kerub.utils.junix.packagemanager.yum

import com.github.K0zka.kerub.host.execute
import org.apache.sshd.ClientSession

abstract class AbstractYum(private val command: String) {

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("$command -y install ${packs.joinToString(separator = " ")}")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("$command -y remove ${packs.joinToString(separator = " ")}")
	}


}