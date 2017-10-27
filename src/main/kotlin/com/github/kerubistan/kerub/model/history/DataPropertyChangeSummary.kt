package com.github.kerubistan.kerub.model.history

data class DataPropertyChangeSummary(
		override val property: String,
		val changes : Map<String, PropertyChangeSummary>
) : PropertyChangeSummary