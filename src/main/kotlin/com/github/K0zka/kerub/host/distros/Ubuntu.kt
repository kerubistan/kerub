package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.between
import com.github.K0zka.kerub.utils.silent

class Ubuntu : AbstractDebian("Ubuntu") {
	override fun handlesVersion(version: Version): Boolean =
			silent { version.major.toInt().between(12, 17) } ?: false
}