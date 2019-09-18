package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("data-change")
data class DataPropertyChangeSummary(
		override val property: String,
		val changes : Map<String, PropertyChangeSummary>
) : PropertyChangeSummary