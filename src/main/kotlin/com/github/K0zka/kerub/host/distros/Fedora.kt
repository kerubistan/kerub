package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.rpm.RpmListPackages
import org.apache.sshd.ClientSession

public class Fedora : LsbDistribution("Fedora") {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
		RpmListPackages.execute(session)

	override fun handlesVersion(version: Version): Boolean {
		return version.major in "19".."21"
	}

	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("yum -y install ${pack}")
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("yum -y remove ${pack}")
	}
}