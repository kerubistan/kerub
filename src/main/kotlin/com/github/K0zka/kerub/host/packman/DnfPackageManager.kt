package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Dnf
import org.apache.sshd.ClientSession

class DnfPackageManager(session : ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(pack: String) {
		Dnf.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Dnf.uninstallPackage(session, pack)
	}
}