package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.utils.version.Version
import org.hibernate.search.annotations.Field
import java.io.Serializable

public data class SoftwarePackage(
		Field val name: String,
		Field val version: Version) : Serializable {
	override
	fun toString(): String {
		return "${name}-${version}"
	}
}