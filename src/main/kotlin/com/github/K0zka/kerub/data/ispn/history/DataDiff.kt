package com.github.K0zka.kerub.data.ispn.history

import com.github.K0zka.kerub.model.history.IgnoreDiff
import com.github.K0zka.kerub.model.history.PropertyChange
import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> diff(oldData: T, newData: T): List<PropertyChange> =
		T::class.memberProperties.filter { it.annotations.none { it is IgnoreDiff } }.map {
			property ->
			val oldValue = property.get(oldData)
			val newValue = property.get(newData)
			if (oldValue != newValue) {
				PropertyChange(property = property.name, oldValue = oldValue, newValue = newValue)
			} else null
		}.filterNotNull()
