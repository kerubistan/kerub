package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.between
import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Yum
import com.github.K0zka.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import org.apache.sshd.ClientSession

public open class Fedora : LsbDistribution("Fedora") {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
		RpmListPackages.execute(session)

	override fun handlesVersion(version: Version): Boolean {
		return version.major.between("19", "21")
	}

	override fun installPackage(pack: String, session: ClientSession) {
		Yum.installPackage(session, pack)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		Yum.uninstallPackage(session, pack)
	}
}