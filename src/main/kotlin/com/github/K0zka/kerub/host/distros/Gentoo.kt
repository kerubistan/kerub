package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.ClientSession

public class Gentoo : AbstractLinux() {
	override fun getVersion(session: ClientSession): Version {
		throw UnsupportedOperationException()
	}

	override fun name(): String {
		throw UnsupportedOperationException()
	}

	override fun listPackages(session: ClientSession): List<SoftwarePackage> {
		throw UnsupportedOperationException()
	}

	override fun handlesVersion(version: Version): Boolean {
		throw UnsupportedOperationException()
	}

	override fun detect(session: ClientSession): Boolean {
		throw UnsupportedOperationException()
	}

	override fun installPackage(pack: String, session: ClientSession) {
		throw UnsupportedOperationException()
	}

	override fun uninstallPackage(pack: String, session: ClientSession) {
		throw UnsupportedOperationException()
	}

}