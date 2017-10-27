package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.host.PackageManager
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import org.apache.sshd.client.session.ClientSession

abstract class AbstractRpmBasedPackageManager(protected val session: ClientSession) : PackageManager {
	override fun list(): List<SoftwarePackage> =
			RpmListPackages.execute(session)
}