package com.github.kerubistan.kerub.utils

import com.github.kerubistan.kerub.model.Entity
import io.github.kerubistan.kroki.collections.join

operator fun <X, Y> Collection<X>.times(other: Collection<Y>): List<Pair<X, Y>> {
	return this.map { x -> other.map { y -> x to y } }.join()
}

inline fun <reified C : Any> Iterable<*>.hasAny(predicate : (C) -> Boolean = { true })
		= this.any { it is C && predicate(it) }

inline fun <reified C : Any> Iterable<*>.any() = this.any { it is C }

inline fun <reified C : Any> Iterable<*>.none() = this.none { it is C }

fun <T> List<T>.skip(): List<T> =
		if (this.isEmpty()) {
			listOf()
		} else {
			this.subList(1, this.size)
		}

fun <T> Collection<T>.containsAny(vararg elems: T) =
		elems.any {
			this.contains(it)
		}

fun <K, V : Entity<K>> Collection<V>.toMap(): Map<K, V> =
		this.associateBy { it.id }

fun <T> Collection<T>.avgBy(fn: (T) -> Int): Double {
	var sum = 0
	this.forEach { sum += fn(it) }
	return sum.toDouble() / this.size
}

// TODO should be moved to kroki
inline fun <T> List<T>.update(
		selector: (T) -> Boolean, default: () -> T = { throw IllegalArgumentException("key not found") }, map: (T) -> T
): List<T> =
		this.firstOrNull(selector)?.let {
			this.filterNot(selector) + map(it)
		} ?: this + map(default())

// TODO should be moved to kroki
inline fun <X, reified T : X> List<X>.updateInstances(
		selector: (T) -> Boolean = { true },
		map : (T) -> T ) : List<X> = this.map { item ->
	if(item is T && selector(item)) {
		map(item)
	} else item
}

// I need to think about this, but this may be just too specific for kroki
inline fun <R : Any, L, reified SUB : R, reified P> List<R>.mergeInstancesWith(
		leftItems: Iterable<L>,
		rightValue: (SUB) -> P,
		leftValue: (L) -> P,
		merge: (SUB, L) -> SUB,
		miss: (SUB) -> R? = { _ -> null },
		missLeft: (L) -> R? = { _ -> null }
): List<R> {
	val leftValuesByProp = leftItems.associateBy(leftValue)
	val rightValuesByProp = this.filterIsInstance<SUB>().associateBy(rightValue)

	return leftValuesByProp.filterKeys { it !in rightValuesByProp.keys }.mapNotNull { (_, v) -> missLeft(v) } +
			this.mapNotNull { item ->
				if (item is SUB) {
					val rightCounterpart = leftValuesByProp[rightValue(item)]
					if (rightCounterpart != null) {
						merge(item, rightCounterpart)
					} else {
						miss(item)
					}
				} else {
					item
				}
			}
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

fun <T> List<T>.subLists(minLength: Int = 1, selector : (T) -> Boolean) : List<List<T>> {
	var ret = listOf<List<T>>()

	var start : Int? = null
	for(idx in 0..(this.size - 1)) {
		val match = selector(this[idx])
		if(start != null) {
			if(!match) {
				if (idx - 1 - start > minLength) {
					ret += listOf(this.subList(start, idx))
				}
				start = null
			}
		} else if(match) {
			start = idx
		}
	}
	if(start != null && this.size - 1 - start >= minLength) {
		ret += listOf(this.subList(start, this.size + 1))
	}

	return ret
}

fun <I, E : Entity<I>> Collection<E>.byId() = this.associateBy { it.id }

inline fun <reified C : Any, R : Any> Iterable<*>.mapInstances(predicate: (C) -> R?) = this.mapNotNull {
	if (it is C) {
		predicate(it)
	} else null
}

operator fun <T> Collection<T>?.contains(element: T): Boolean =
		this?.contains(element) ?: false
