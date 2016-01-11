package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.DnfPackageManager
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.silent
import org.apache.sshd.ClientSession

class Fedora23Plus : Fedora() {
	override fun getPackageManager(session: ClientSession): PackageManager {
		return DnfPackageManager(session)
	}

	override fun handlesVersion(version: Version): Boolean =
			silent { version.major.toInt() >= 23 } ?: false
}