package com.github.K0zka.kerub.host.packman

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.utils.junix.packagemanager.cygwin.CygCheck
import org.apache.sshd.client.session.ClientSession

class CygwinPackageManager(private val session: ClientSession) : PackageManager {
	override fun install(vararg pack: String) {
		notImplemented()
	}

	override fun remove(vararg pack: String) {
		notImplemented()
	}

	private fun notImplemented() {
		throw NotImplementedError("Can't install/remove packages in cygwin")
	}

	override fun list(): List<SoftwarePackage> =
			CygCheck.listPackages(session) // TODO: https://github.com/kerubistan/kerub/issues/94 - include wmic list

}