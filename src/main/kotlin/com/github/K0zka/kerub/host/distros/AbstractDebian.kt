package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.host.packman.AptPackageManager
import org.apache.sshd.ClientSession

abstract class AbstractDebian(distroName: String) : LsbDistribution(distroName) {
	override fun getPackageManager(session: ClientSession): PackageManager
			= AptPackageManager(session)
}