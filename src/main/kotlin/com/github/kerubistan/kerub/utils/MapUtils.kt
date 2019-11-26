package com.github.kerubistan.kerub.utils

fun <K, V> Map<K, V>.inverse() =
		this.map { it.value to it.key }.toMap()

fun <K, V> MutableMap<K, V>.updateMutable(key: K, mapper: (V) -> V, init: () -> V) {
	val value = this[key]
	if (value == null) {
		this += (key to init())
	} else {
		this[key] = mapper(value)
	}
}

