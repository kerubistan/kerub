package com.github.K0zka.kerub.model.history

import java.io.Serializable

data class PropertyChange (
		val property : String,
		val oldValue : Any?,
		val newValue : Any?
) : Serializable