package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.utils.junix.packagemanager.apt.Apt
import org.apache.sshd.ClientSession

class AptPackageManager(private val session : ClientSession) : PackageManager {
	override fun install(pack: String) {
		Apt.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Apt.uninstallPackage(session, pack)
	}
}