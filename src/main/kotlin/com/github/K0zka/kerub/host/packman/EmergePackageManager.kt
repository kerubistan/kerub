package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.emerge.Emerge
import org.apache.sshd.client.session.ClientSession

class EmergePackageManager(private val session: ClientSession) : PackageManager {
	override fun install(vararg pack: String) {
		//since gentoo software installation is both sophisticated, time consuming and CPU instensive on the host,
		// this should not be done, but needs a cleanup
		TODO("https://github.com/kerubistan/kerub/issues/20")
	}

	override fun remove(vararg pack: String) {
		//since gentoo software installation is both sophisticated, time consuming and CPU instensive on the host,
		// this should not be done, but needs a cleanup
		TODO("https://github.com/kerubistan/kerub/issues/20")
	}

	override fun list(): List<SoftwarePackage> =
			Emerge.listPackages(session)
}