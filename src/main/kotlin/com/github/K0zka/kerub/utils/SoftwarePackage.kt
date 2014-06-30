package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.utils.version.Version
import org.hibernate.search.annotations.Field

public data class SoftwarePackage(Field val name: String, Field val version: Version) {
	override
	fun toString(): String {
		return "${name}-${version}"
	}
}