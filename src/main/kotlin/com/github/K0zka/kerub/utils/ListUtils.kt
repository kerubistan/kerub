package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.model.Entity

operator fun <X, Y> Collection<X>.times(other: Collection<Y>): List<Pair<X, Y>> {
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

fun <T> Collection<T>.containsAny(vararg elems: T) =
		elems.any {
			this.contains(it)
		}

fun <K, V : Entity<K>> Collection<V>.toMap(): Map<K, V> =
		this.map { it.id to it }.toMap()

fun <T> Collection<T>.avgBy(fn: (T) -> Int): Double {
	var sum: Int = 0
	this.forEach { sum += fn(it) }
	return sum.toDouble() / this.size
}
