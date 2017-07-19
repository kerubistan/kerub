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

/**
 * Update a list with another, different type of items. Not updated items will remain the same, updates
 * not matching a data will be ignored.
 * @param updateList    a list of elements with which the list is to be updated
 * @param selfKey       extract the join key from the original list
 * @param upKey         extract the join key from the update list
 * @param merge         creates an updated instance from the original
 */
inline fun <T : Any, U : Any, I : Any> List<T>.update(
		updateList: List<U>,
		selfKey: (T) -> I,
		upKey: (U) -> I,
		merge: (T, U) -> T
): List<T> = this.update(updateList, selfKey, upKey, merge, { it }, { null })

/**
 * Update a list with another, different type of items
 * @param updateList    a list of elements with which the list is to be updated
 * @param selfKey       extract the join key from the original list
 * @param upKey         extract the join key from the update list
 * @param merge         creates an updated instance from the original
 * @param updateMiss	handle items that did not get updated
 * @param selfMiss		handle updates that do not match any data
 */
inline fun <T : Any, U : Any, I : Any> List<T>.update(
		updateList: List<U>,
		selfKey: (T) -> I,
		upKey: (U) -> I,
		merge: (T, U) -> T,
		updateMiss: (T) -> T?,
		selfMiss: (U) -> T?
): List<T> {
	val selfMap = this.associateBy(selfKey)
	val updateMap = updateList.associateBy(upKey)
	return selfMap.map { (key, value) ->
		updateMap[key]?.let { merge(value, it) } ?: updateMiss(value)
	}.filterNotNull() + updateMap.filterNot { selfMap.containsKey(it.key) }.map {
		selfMiss(it.value)
	}.filterNotNull()
}
