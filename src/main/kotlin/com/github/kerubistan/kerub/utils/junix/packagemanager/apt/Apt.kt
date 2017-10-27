package com.github.kerubistan.kerub.utils.junix.packagemanager.apt

import com.github.kerubistan.kerub.host.executeOrDie
import org.apache.sshd.client.session.ClientSession

object Apt {

	fun installPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.executeOrDie("apt-get -y install ${packs.joinToString(separator = " ")}")
	}

	fun uninstallPackage(session: ClientSession, vararg packs: String) {
		require(packs.isNotEmpty())
		session.executeOrDie("apt-get -y remove ${packs.joinToString(separator = " ")}")
	}

}