package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.rpm.RpmListPackages
import org.apache.sshd.ClientSession

abstract class AbstractRpmBasedPackageManager(protected val session: ClientSession) : PackageManager {
	override fun list(): List<SoftwarePackage> =
			RpmListPackages.execute(session)
}