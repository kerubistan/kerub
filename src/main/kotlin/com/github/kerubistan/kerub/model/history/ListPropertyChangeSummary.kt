package com.github.kerubistan.kerub.model.history

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("list-change")
data class ListPropertyChangeSummary(
		override val property: String,
		val changed : Map<Int, PropertyChangeSummary>
) : PropertyChangeSummary