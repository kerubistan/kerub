package com.github.kerubistan.kerub.model.history

import org.codehaus.jackson.annotate.JsonProperty
import java.io.Serializable

data class PropertyChange(
		val property: String,
		@JsonProperty("old")
		val oldValue: Serializable?,
		@JsonProperty("new")
		val newValue: Serializable?
) : Serializable