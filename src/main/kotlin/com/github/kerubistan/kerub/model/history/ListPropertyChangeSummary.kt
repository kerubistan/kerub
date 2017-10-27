package com.github.kerubistan.kerub.model.history

data class ListPropertyChangeSummary(
		override val property: String,
		val changed : Map<Int, PropertyChangeSummary>
) : PropertyChangeSummary