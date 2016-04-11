package com.github.K0zka.kerub.utils.junix.packagemanager.zypper

import com.github.K0zka.kerub.host.execute
import org.apache.sshd.client.session.ClientSession

object Zypper {

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("zypper -n install ${packs.joinToString(separator = " ")}")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.execute("zypper -n remove ${packs.joinToString(separator = " ")}")
	}

}