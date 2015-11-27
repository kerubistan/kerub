package com.github.K0zka.kerub.utils.junix.packagemanager.apt

import com.github.K0zka.kerub.host.execute
import org.apache.sshd.ClientSession

object Apt {

	fun installPackage(session: ClientSession, pack: String) {
		session.execute("apt-get -y install ${pack}")
	}

	fun uninstallPackage(session: ClientSession, pack: String) {
		session.execute("apt-get -y remove ${pack}")
	}

}