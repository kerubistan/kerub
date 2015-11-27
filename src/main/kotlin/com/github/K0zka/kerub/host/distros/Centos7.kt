package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import com.github.K0zka.kerub.utils.junix.packagemanager.yum.Yum
import org.apache.sshd.ClientSession

public class Centos7 : LsbDistribution("Centos Linux") {
	override fun handlesVersion(version: Version): Boolean {
		return version.major == "7"
	}

	override fun installPackage(pack: String, session: ClientSession) {
		Yum.installPackage(session, pack)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		Yum.uninstallPackage(session, pack)
	}

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
			RpmListPackages.execute(session)
}