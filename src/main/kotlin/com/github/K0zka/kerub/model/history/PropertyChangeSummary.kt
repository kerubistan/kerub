package com.github.K0zka.kerub.model.history

import java.io.Serializable

data class PropertyChangeSummary(
		val property: String,
		val min: Any?,
		val max: Any?,
		val average: Any?,
		val extremes: List<PropertyChange>
) : Serializable