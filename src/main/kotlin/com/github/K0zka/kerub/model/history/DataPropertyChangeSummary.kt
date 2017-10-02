package com.github.K0zka.kerub.model.history

data class DataPropertyChangeSummary(
		override val property: String,
		val changes : Map<String, PropertyChangeSummary>
) : PropertyChangeSummary