package com.github.K0zka.kerub.utils

/**
 * Returns the element from the array, or null if the index is outside the boundary.
 */
public fun <T> Array<out T>.elemOrNull(index : Int): T? {
	return if (size() > index) this[index] else null
}