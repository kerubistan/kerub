package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.utils.junix.packagemanager.yum.Yum
import org.apache.sshd.client.session.ClientSession

class YumPackageManager(session: ClientSession) : AbstractRpmBasedPackageManager(session) {
	override fun install(vararg pack: String) {
		Yum.installPackage(session, *pack)
	}

	override fun remove(vararg pack: String) {
		Yum.uninstallPackage(session, *pack)
	}
}