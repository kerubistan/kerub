package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession

public class Raspbian : AbstractDebian("Raspbian GNU/Linux") {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> {
		return session.execute(
				"dpkg-query -W")
				.trim()
				.split('\n').map {
			parseDpkgOutputLine(it)
		}
	}

	override fun handlesVersion(version: Version): Boolean {
		return version.major == "7"
	}
}