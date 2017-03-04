package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.zypper.Zypper
import org.apache.sshd.client.session.ClientSession

class ZypperPackageManager(session: ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg pack: String) {
		Zypper.installPackage(session, *pack)
	}

	override fun remove(vararg pack: String) {
		Zypper.uninstallPackage(session, *pack)
	}
}