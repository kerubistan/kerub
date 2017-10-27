package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.packman.YumPackageManager
import com.github.kerubistan.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class Centos7 : LsbDistribution("Centos Linux") {
	override fun getPackageManager(session: ClientSession): PackageManager
			= YumPackageManager(session)

	override fun handlesVersion(version: Version): Boolean {
		return version.major == "7"
	}

}