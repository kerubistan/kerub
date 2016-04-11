package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.packman.ZypperPackageManager
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import org.apache.sshd.client.session.ClientSession

class OpenSuse : LsbDistribution("openSUSE") {
	override fun getPackageManager(session: ClientSession) = ZypperPackageManager(session)

	companion object {
		private val packages = mapOf<OsCommand, List<String>>(
				TgtAdmin to listOf("tgt")
		)
	}

	override fun handlesVersion(version: Version): Boolean
			= version.major == "13"

	override fun getRequiredPackages(osCommand: OsCommand): List<String> {
		return packages[osCommand] ?: super.getRequiredPackages(osCommand)
	}
}