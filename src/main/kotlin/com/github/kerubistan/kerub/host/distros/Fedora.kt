package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.packman.YumPackageManager
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.between
import com.github.kerubistan.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

open class Fedora : LsbDistribution("Fedora") {
	override fun getPackageManager(session: ClientSession): PackageManager {
		return YumPackageManager(session)
	}

	override fun handlesVersion(version: Version) =
			silent(level = LogLevel.Info) { version.major.toInt().between(19, 22) } ?: false
}