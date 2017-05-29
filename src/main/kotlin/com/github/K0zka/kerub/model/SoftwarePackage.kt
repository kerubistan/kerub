package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.search.annotations.Field
import java.io.Serializable

data class SoftwarePackage(
		@JsonProperty("name")
		@Field val name: String,
		@JsonProperty("ver")
		@Field val version: Version) : Serializable {
	override
	fun toString(): String {
		return "$name-$version"
	}
}