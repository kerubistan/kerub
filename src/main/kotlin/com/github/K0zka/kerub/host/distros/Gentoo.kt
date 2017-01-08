package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.EmergePackageManager
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class Gentoo : LsbDistribution("Gentoo") {
	override fun getPackageManager(session: ClientSession): PackageManager = EmergePackageManager(session)

	override fun getVersion(session: ClientSession): Version
			= Version.fromVersionString("0")


	override fun handlesVersion(version: Version): Boolean = true // since gentoo does not really have releases


}