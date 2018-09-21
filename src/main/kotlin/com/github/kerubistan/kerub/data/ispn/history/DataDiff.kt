package com.github.kerubistan.kerub.data.ispn.history

import com.github.kerubistan.kerub.model.history.IgnoreDiff
import com.github.kerubistan.kerub.model.history.PropertyChange
import java.io.Serializable
import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> diff(oldData: T, newData: T): List<PropertyChange> =
		T::class.memberProperties.filter { it.annotations.none { it is IgnoreDiff } }.map {
			property ->
			val oldValue = property.get(oldData) as Serializable?
			val newValue = property.get(newData) as Serializable?
			if (oldValue != newValue) {
				PropertyChange(property = property.name, oldValue = oldValue, newValue = newValue)
			} else null
		}.filterNotNull()
