package com.github.kerubistan.kerub.utils

private const val hundred = 100.toDouble()

fun Int.asPercentOf(full: Int): Int = if (this == 0) 0 else {
	(this / (full / hundred)).toInt()
}