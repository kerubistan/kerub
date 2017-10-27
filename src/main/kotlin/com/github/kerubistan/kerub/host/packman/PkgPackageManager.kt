package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.packagemanager.pkg.Pkg
import org.apache.sshd.client.session.ClientSession

class PkgPackageManager(private val session: ClientSession) : PackageManager {
	override fun install(vararg pack: String) {
		Pkg.installPackage(session, *pack)
	}

	override fun remove(vararg pack: String) {
		Pkg.uninstallPackage(session, *pack)
	}

	override fun list(): List<SoftwarePackage> = Pkg.listPackages(session)
}