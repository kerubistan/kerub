package com.github.kerubistan.kerub.model.history

import java.io.Serializable

data class PropertyChange(
		val property: String,
		val oldValue: Any?,
		val newValue: Any?
) : Serializable