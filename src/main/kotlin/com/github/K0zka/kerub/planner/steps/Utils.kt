package com.github.K0zka.kerub.planner.steps

fun <T> Collection<T>.replace(
		filter : (T) -> Boolean,
        replacer : (T) -> T
                       ) : List<T> {
	return this.map {
		if(filter(it)) {
			replacer(it)
		} else {
			it
		}
	}
}