package com.github.K0zka.kerub.host.distros

import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.utils.between

public class Ubuntu() : AbstractDebian("Ubuntu") {
	override fun handlesVersion(version: Version): Boolean {
		return version.major.between("12", "14")
	}
}