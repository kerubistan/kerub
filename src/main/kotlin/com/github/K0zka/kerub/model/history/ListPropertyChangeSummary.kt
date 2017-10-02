package com.github.K0zka.kerub.model.history

data class ListPropertyChangeSummary(
		override val property: String,
		val changed : Map<Int, PropertyChangeSummary>
) : PropertyChangeSummary