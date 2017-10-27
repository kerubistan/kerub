package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.packagemanager.apt.Apt
import com.github.kerubistan.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.client.session.ClientSession

open class AptPackageManager(protected val session: ClientSession) : PackageManager {
	override fun list(): List<SoftwarePackage> =
			Dpkg.listPackages(session)

	override fun install(vararg pack: String) {
		Apt.installPackage(session, *pack)
	}

	override fun remove(vararg pack: String) {
		Apt.uninstallPackage(session, *pack)
	}
}