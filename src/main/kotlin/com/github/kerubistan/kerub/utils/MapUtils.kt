package com.github.kerubistan.kerub.utils

fun <K : Any, V : Any> Map<K, V>.want(key: K): V = requireNotNull(this[key]) { "key $key not found" }

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

fun <K, V> Map<K, V>.update(key: K, mapper: (V) -> V, init: () -> V): Map<K, V> =
		this[key]?.let { this + (key to mapper(it)) } ?: this+(key to init())

fun <K : Any, V : Any> Map<K, V>.update(key: K, mapper: (V) -> V): Map<K, V> =
		this.mapValues {
			if(it.key == key) {
				mapper(it.value)
			} else {
				it.value
			}
		}
