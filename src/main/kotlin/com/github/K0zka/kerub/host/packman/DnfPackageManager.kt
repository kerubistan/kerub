package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Dnf
import org.apache.sshd.client.session.ClientSession

class DnfPackageManager(session: ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg packs: String) {
		Dnf.installPackage(session, *packs)
	}

	override fun remove(vararg packs: String) {
		Dnf.uninstallPackage(session, *packs)
	}
}