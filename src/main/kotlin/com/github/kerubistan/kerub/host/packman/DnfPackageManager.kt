package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.utils.junix.packagemanager.yum.Dnf
import org.apache.sshd.client.session.ClientSession

class DnfPackageManager(session: ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg pack: String) {
		Dnf.installPackage(session, *pack)
	}

	override fun remove(vararg pack: String) {
		Dnf.uninstallPackage(session, *pack)
	}
}