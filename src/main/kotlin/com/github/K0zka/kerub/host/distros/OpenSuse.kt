package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import org.apache.sshd.ClientSession

public class OpenSuse : LsbDistribution("openSUSE") {
	override fun handlesVersion(version: Version): Boolean
			= version.major == "13"

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
			RpmListPackages.execute(session)


	override fun installPackage(pack: String, session: ClientSession) {
		session.execute("zypper -n install ${pack}")
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		session.execute("zypper -n remove ${pack}")
	}
}