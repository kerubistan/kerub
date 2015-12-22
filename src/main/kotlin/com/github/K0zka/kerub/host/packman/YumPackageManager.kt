package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Yum
import org.apache.sshd.ClientSession

class YumPackageManager(private val session : ClientSession) : PackageManager {
	override fun install(pack: String) {
		Yum.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Yum.uninstallPackage(session, pack)
	}
}