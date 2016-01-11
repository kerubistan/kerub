package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Yum
import org.apache.sshd.ClientSession

class YumPackageManager(session : ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(pack: String) {
		Yum.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Yum.uninstallPackage(session, pack)
	}
}