package com.github.K0zka.kerub.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * A serializable range.
 */
data class Range<T>(
		@JsonProperty("min")
		val min: T,
		@JsonProperty("max")
		val max: T) : Serializable
