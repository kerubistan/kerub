package com.github.kerubistan.kerub.utils

fun <K : Any, V : Any> Map<K, V>.want(key: K): V = requireNotNull(this[key]) { "key $key not found" }

fun <K, V> Map<K, V>.inverse() =
		this.map { it.value to it.key }.toMap()

fun <K, V> Map<K, V>.update(key: K, mapper: (V) -> V, init: () -> V): Map<K, V> {
	val value = this[key]
	if (value == null) {
		return this + (key to init())
	} else {
		return this + (key to mapper(value))
	}
}

fun <K : Any, V : Any> Map<K, V>.update(key: K, mapper: (V) -> V): Map<K, V> {
	return this + (key to mapper(requireNotNull(this[key])))
}
