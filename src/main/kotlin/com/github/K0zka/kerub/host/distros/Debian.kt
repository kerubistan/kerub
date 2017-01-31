package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version

class Debian : AbstractDebian("Debian GNU/Linux") {
	override fun handlesVersion(version: Version): Boolean = version.major == "8"
}