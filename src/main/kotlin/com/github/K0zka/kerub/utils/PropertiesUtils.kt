package com.github.K0zka.kerub.utils

import java.util.Properties

fun propertiesOf(vararg params: Pair<String, String>): Properties = Properties().let { props ->
	params.forEach {
		props.setProperty(it.first, it.second)
	}
	props
}

inline fun Properties.filterByKeys(filter: (String) -> Boolean): Properties =
		Properties().let { newProps ->
			this.forEach {
				if (filter(it.key.toString())) {
					newProps.setProperty(it.key.toString(), it.value.toString())
				}
			}
			newProps
		}