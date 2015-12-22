package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Dnf
import org.apache.sshd.ClientSession

class DnfPackageManager(private val session : ClientSession) : PackageManager {
	override fun install(pack: String) {
		Dnf.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Dnf.uninstallPackage(session, pack)
	}
}