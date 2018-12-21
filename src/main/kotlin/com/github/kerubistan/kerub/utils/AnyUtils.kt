package com.github.kerubistan.kerub.utils

fun <T> T.equalsAnyOf(vararg others: T) = others.any { this == it }

fun String.equalsAnyIgnoreCase(vararg others: String) = others.any { it.equals(this, ignoreCase = true) }

/**
 * Proudly and intentionally doing nothing.
 */
inline fun NOP(): Unit { /*the expected nothing, inlined*/ }
