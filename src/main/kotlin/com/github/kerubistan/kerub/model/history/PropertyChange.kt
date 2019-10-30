package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PropertyChange(
		val property: String,
		@JsonProperty("old")
		val oldValue: Serializable?,
		@JsonProperty("new")
		val newValue: Serializable?
) : Serializable