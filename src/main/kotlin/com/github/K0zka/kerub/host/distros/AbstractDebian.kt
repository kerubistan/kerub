package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.apt.Apt
import com.github.K0zka.kerub.utils.junix.packagemanager.dpkg.Dpkg
import org.apache.sshd.ClientSession

public abstract class AbstractDebian(distroName: String) : LsbDistribution(distroName) {

	override fun listPackages(session: ClientSession): List<SoftwarePackage> = Dpkg.listPackages(session)


	override fun installPackage(pack: String, session: ClientSession) {
		Apt.installPackage(session, pack)
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		Apt.uninstallPackage(session, pack)
	}


}