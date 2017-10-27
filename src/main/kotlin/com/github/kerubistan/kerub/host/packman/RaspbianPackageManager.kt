package com.github.kerubistan.kerub.host.packman

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.client.session.ClientSession

class RaspbianPackageManager(session: ClientSession) : AptPackageManager(session) {
	override fun list(): List<SoftwarePackage> = Dpkg.listRasbianPackages(session)
}