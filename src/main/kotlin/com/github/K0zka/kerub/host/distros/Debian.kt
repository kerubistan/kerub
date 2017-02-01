package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version

class Debian : AbstractDebian("Debian GNU/Linux") {
	override fun handlesVersion(version: Version): Boolean = version.major == "8"
	//get rid of "GNU/Linux", not relevant here
	override fun name(): String = "Debian"
}