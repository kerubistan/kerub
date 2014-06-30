package com.github.K0zka.kerub.host.distros

import org.apache.sshd.ClientSession
import com.github.K0zka.kerub.utils.version.Version
import com.github.K0zka.kerub.utils.SoftwarePackage

public class Gentoo : Distribution {
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