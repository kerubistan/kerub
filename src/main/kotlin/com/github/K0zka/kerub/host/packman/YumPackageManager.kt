package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Yum
import org.apache.sshd.ClientSession

class YumPackageManager(session : ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg packs: String) {
		Yum.installPackage(session, *packs)
	}

	override fun remove(vararg packs: String) {
		Yum.uninstallPackage(session, *packs)
	}
}