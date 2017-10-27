package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.packman.EmergePackageManager
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class Gentoo : LsbDistribution("Gentoo") {
	override fun getPackageManager(session: ClientSession): PackageManager = EmergePackageManager(session)

	override fun getVersion(session: ClientSession): Version
			= Version.fromVersionString("0")


	override fun handlesVersion(version: Version): Boolean = true // since gentoo does not really have releases

	override fun installMonitorPackages(session: ClientSession) {
		//do nothing, at the moment it is not possible to install monitoring packages on gentoo
	}

}