package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.zypper.Zypper
import org.apache.sshd.ClientSession

class ZypperPackageManager(session : ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(pack: String) {
		Zypper.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Zypper.uninstallPackage(session, pack)
	}
}