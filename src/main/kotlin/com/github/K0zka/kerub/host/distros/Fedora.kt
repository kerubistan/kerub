package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.YumPackageManager
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.between
import com.github.K0zka.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

open class Fedora : LsbDistribution("Fedora") {
	override fun getPackageManager(session: ClientSession): PackageManager {
		return YumPackageManager(session)
	}

	override fun handlesVersion(version: Version) =
			silent { version.major.toInt().between(19, 22) } ?: false
}