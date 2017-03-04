package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.pkg.Pkg
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