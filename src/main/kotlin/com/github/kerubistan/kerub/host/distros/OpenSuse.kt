package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.checkFileExists
import com.github.kerubistan.kerub.host.packman.ZypperPackageManager
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import com.github.kerubistan.kerub.utils.silent
import org.apache.sshd.client.session.ClientSession

class OpenSuse : LsbDistribution("openSUSE") {
	override fun getPackageManager(session: ClientSession) = ZypperPackageManager(session)

	override fun detect(session: ClientSession): Boolean = session.checkFileExists("/etc/os-release")
			&& silent {
		name().equals(readLsbReleaseProperties(session).get("ID")?.toString(), ignoreCase = true)
	} ?: false

	companion object {
		private val packages = mapOf<OsCommand, List<String>>(
				TgtAdmin to listOf("tgt")
		)
	}

	override fun handlesVersion(version: Version): Boolean = version.major == "42"

	override fun getRequiredPackages(osCommand: OsCommand): List<String> {
		return packages[osCommand] ?: super.getRequiredPackages(osCommand)
	}
}