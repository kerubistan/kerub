package com.github.kerubistan.kerub.host.distros

import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.between
import com.github.kerubistan.kerub.utils.silent

class Ubuntu : AbstractDebian("Ubuntu") {
	override fun handlesVersion(version: Version): Boolean =
			silent(level = LogLevel.Info) { version.major.toInt().between(12, 17) } ?: false
}