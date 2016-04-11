package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.apt.Apt
import com.github.K0zka.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.client.session.ClientSession

open class AptPackageManager(protected val session: ClientSession) : PackageManager {
	override fun list(): List<SoftwarePackage> =
			Dpkg.listPackages(session)

	override fun install(vararg packs: String) {
		Apt.installPackage(session, *packs)
	}

	override fun remove(vararg packs: String) {
		Apt.uninstallPackage(session, *packs)
	}
}