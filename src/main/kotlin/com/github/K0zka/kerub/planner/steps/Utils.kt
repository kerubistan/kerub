package com.github.K0zka.kerub.planner.steps

inline fun <T> factoryFeature(enabled: Boolean, producer: () -> List<T>): List<T> =
		if (enabled) {
			producer()
		} else {
			listOf<T>()
		}

/*
 * TODO: issue #120 - move this whole thing to utils package
 */

fun <T> Collection<T>.replace(
		filter: (T) -> Boolean,
		replacer: (T) -> T
): List<T> {
	return this.map {
		if (filter(it)) {
			replacer(it)
		} else {
			it
		}
	}
}
