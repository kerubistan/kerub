package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version

public class Ubuntu() : AbstractDebian("Ubuntu") {
	override fun handlesVersion(version: Version): Boolean {
		return version.major in "12".."14"
	}
}