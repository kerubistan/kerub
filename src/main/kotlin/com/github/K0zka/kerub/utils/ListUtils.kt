package com.github.K0zka.kerub.utils

fun <T> Collection<T>.only() : T {
	require(this.size == 1) { "List size (${size}) is not 1" }
	return first()
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