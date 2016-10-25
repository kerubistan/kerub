package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.model.Entity

fun <T> Collection<T>.only(): T {
	require(this.size == 1) { "List size (${size}) is not 1" }
	return first()
}

operator fun <X, Y> Collection<X>.times(other : Collection<Y>): List<Pair<X, Y>> {
	return this.map { x -> other.map { y -> x to y } }.join()
}

fun <T> List<T>.skip(): List<T> =
		if (this.isEmpty()) {
			listOf<T>()
		} else {
			this.subList(1, this.size)
		}

fun <T> Collection<Collection<T>>.join(): List<T> {
	var result = listOf<T>()
	this.forEach { result += it }
	return result
}

fun <T> Collection<T>.containsAny(vararg elems : T) =
	elems.any {
		this.contains(it)
	}


fun <K, V> Collection<V>.toMap(key: (V) -> K): Map<K, V> =
		this.map { key(it) to it }.toMap()

fun <K, V : Entity<K>> Collection<V>.toMap(): Map<K, V> =
		this.map { it.id to it }.toMap()
