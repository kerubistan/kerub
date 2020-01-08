package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.host.packman.AptPackageManager
import org.apache.sshd.client.session.ClientSession

abstract class AbstractDebian(distroName: String) : LsbDistribution(distroName) {
	override fun getPackageManager(session: ClientSession): PackageManager = AptPackageManager(session)
}