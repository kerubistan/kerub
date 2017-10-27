package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.utils.junix.packagemanager.zypper.Zypper
import org.apache.sshd.client.session.ClientSession

class ZypperPackageManager(session: ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg pack: String) {
		Zypper.installPackage(session, *pack)
	}

	override fun remove(vararg pack: String) {
		Zypper.uninstallPackage(session, *pack)
	}
}