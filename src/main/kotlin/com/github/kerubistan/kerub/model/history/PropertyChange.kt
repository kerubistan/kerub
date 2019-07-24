package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

data class PropertyChange(
		val property: String,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val oldValue: Serializable?,
		@JsonInclude(JsonInclude.Include.NON_NULL)
		val newValue: Serializable?
) : Serializable