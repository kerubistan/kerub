package com.github.K0zka.kerub.model.history

data class PropertyChange (
		val property : String,
		val oldValue : Any?,
		val newValue : Any?
)