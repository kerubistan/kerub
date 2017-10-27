package com.github.kerubistan.kerub.utils

fun <T> T.equalsAnyOf(vararg others: T) = others.any { this == it }

inline fun NOP(): Unit {}

inline fun NOP(comment: String): Unit {}