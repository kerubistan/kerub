package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.zypper.Zypper
import org.apache.sshd.ClientSession

class ZypperPackageManager(session : ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg packs: String) {
		Zypper.installPackage(session, *packs)
	}

	override fun remove(vararg packs: String) {
		Zypper.uninstallPackage(session, *packs)
	}
}