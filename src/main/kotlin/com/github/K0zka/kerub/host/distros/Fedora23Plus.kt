package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Dnf
import org.apache.sshd.ClientSession

class Fedora23Plus : Fedora() {
	override fun installPackage(pack: String, session: ClientSession) {
		Dnf.installPackage(session, pack)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		Dnf.uninstallPackage(session, pack)
	}
}