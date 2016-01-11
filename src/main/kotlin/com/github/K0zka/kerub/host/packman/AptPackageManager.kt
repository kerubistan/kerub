package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.apt.Apt
import com.github.K0zka.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.ClientSession

open class AptPackageManager(protected val session: ClientSession) : PackageManager {
	override fun list(): List<SoftwarePackage> =
			Dpkg.listPackages(session)

	override fun install(pack: String) {
		Apt.installPackage(session, pack)
	}

	override fun remove(pack: String) {
		Apt.uninstallPackage(session, pack)
	}
}