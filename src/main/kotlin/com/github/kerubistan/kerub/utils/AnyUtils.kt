package com.github.kerubistan.kerub.utils

fun <T> T.equalsAnyOf(vararg others: T) = others.any { this == it }

fun String.equalsAnyIgnoreCase(vararg others: String) = others.any { it.equals(this, ignoreCase = true) }

inline fun NOP(): Unit {}

inline fun NOP(comment: String): Unit {}