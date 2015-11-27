package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.ClientSession

public class Raspbian : AbstractDebian("Raspbian GNU/Linux") {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
			Dpkg.listRasbianPackages(session)

	override fun handlesVersion(version: Version): Boolean =
			version.major == "7"
}