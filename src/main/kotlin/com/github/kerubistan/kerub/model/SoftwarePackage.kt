package com.github.kerubistan.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.search.annotations.Field
import java.io.Serializable

data class SoftwarePackage(
		@JsonProperty("name")
		@Field val name: String,
		@JsonProperty("ver")
		@Field val version: Version
) : Serializable {

	companion object {
		fun pack(name: String, version: String) =
				SoftwarePackage(name = name, version = Version.fromVersionString(version))
	}

	override fun toString(): String {
		return "$name-$version"
	}
}