package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.host.PackageManager
import com.github.K0zka.kerub.model.Version
import org.apache.sshd.client.session.ClientSession

class Gentoo : AbstractLinux() {
	override fun getPackageManager(session: ClientSession): PackageManager {
		throw TODO("https://github.com/kerubistan/kerub/issues/20")
	}

	override fun getVersion(session: ClientSession): Version {
		throw TODO("https://github.com/kerubistan/kerub/issues/20")
	}

	override fun name(): String {
		throw TODO("https://github.com/kerubistan/kerub/issues/20")
	}

	override fun handlesVersion(version: Version): Boolean {
		throw TODO("https://github.com/kerubistan/kerub/issues/20")
	}

	override fun detect(session: ClientSession): Boolean {
		throw TODO("https://github.com/kerubistan/kerub/issues/20")
	}


}