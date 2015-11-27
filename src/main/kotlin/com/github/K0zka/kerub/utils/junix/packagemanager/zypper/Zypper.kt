package com.github.K0zka.kerub.utils.junix.packagemanager.zypper

import com.github.K0zka.kerub.host.execute
import org.apache.sshd.ClientSession

object Zypper {

	fun installPackage(session: ClientSession, pack: String) {
		session.execute("zypper -n install ${pack}")
	}

	fun uninstallPackage(session: ClientSession, pack: String) {
		session.execute("zypper -n remove ${pack}")
	}

}