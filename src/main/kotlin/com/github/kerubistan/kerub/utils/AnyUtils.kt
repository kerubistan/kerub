package com.github.kerubistan.kerub.utils

import io.github.kerubistan.kroki.collections.join

fun <T> T.equalsAnyOf(vararg others: T) = others.any { this == it }

fun String.equalsAnyIgnoreCase(vararg others: String) = others.any { it.equals(this, ignoreCase = true) }

/**
 * Proudly and intentionally doing nothing.
 */
inline fun NOP(): Unit { /*the expected nothing, inlined*/ }

// moved to kroki, renamed to find
fun <T : Any> T.browse(selector: (T) -> Iterable<T>?, filter: (T) -> Boolean = { true }): Iterable<T> =
		selector(this)?.filter(filter)?.map { listOf(it) + it.browse(selector, filter) }?.join() ?: listOf()
