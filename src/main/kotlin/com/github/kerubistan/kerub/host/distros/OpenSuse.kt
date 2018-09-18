package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.checkFileExists
import com.github.kerubistan.kerub.host.packman.ZypperPackageManager
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

class OpenSuse : LsbDistribution("openSUSE") {
	override fun getPackageManager(session: ClientSession) = ZypperPackageManager(session)

	override fun detect(session: ClientSession): Boolean = session.checkFileExists("/etc/os-release")
			&& silent(level = LogLevel.Info) {
		name().equals(readLsbReleaseProperties(session).get("ID")?.toString(), ignoreCase = true)
	} ?: false

	override fun handlesVersion(version: Version): Boolean = version.major == "42"

}