package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.RaspbianPackageManager
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class Raspbian : AbstractDebian("Raspbian GNU/Linux") {
	override fun getPackageManager(session: ClientSession): PackageManager
			= RaspbianPackageManager(session)

	override fun handlesVersion(version: Version): Boolean =
			version.major == "7"
}