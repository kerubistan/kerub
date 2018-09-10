package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.packman.DnfPackageManager
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

class Fedora23Plus : Fedora() {
	override fun getPackageManager(session: ClientSession): PackageManager {
		return DnfPackageManager(session)
	}

	override fun handlesVersion(version: Version): Boolean =
			silent(level = LogLevel.Info) { version.major.toInt() >= 23 } ?: false
}