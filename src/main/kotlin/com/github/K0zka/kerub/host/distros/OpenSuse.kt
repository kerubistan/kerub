package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.execute
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.junix.common.OsCommand
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin
import com.github.K0zka.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import com.github.K0zka.kerub.utils.junix.packagemanager.zypper.Zypper
import org.apache.sshd.ClientSession

public class OpenSuse : LsbDistribution("openSUSE") {

	companion object {
		private val packages = mapOf<OsCommand, List<String>>(
				TgtAdmin to listOf("tgt")
		)
	}

	override fun handlesVersion(version: Version): Boolean
			= version.major == "13"

	override fun listPackages(session: ClientSession): List<SoftwarePackage> =
			RpmListPackages.execute(session)


	override fun installPackage(pack: String, session: ClientSession) {
		Zypper.installPackage(session, pack)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		Zypper.uninstallPackage(session, pack)
	}

	override fun getRequiredPackages(osCommand: OsCommand): List<String> {
		return packages[osCommand] ?: super.getRequiredPackages(osCommand)
	}
}