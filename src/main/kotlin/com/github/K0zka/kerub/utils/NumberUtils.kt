package com.github.K0zka.kerub.utils

import java.math.BigInteger

fun <T : Comparable<T>> T.between(lower: T, higher: T): Boolean =
		this >= lower && this <= higher

fun Collection<BigInteger>.sum(): BigInteger {
	var sum: BigInteger = BigInteger.ZERO
	this.forEach { sum += it }
	return sum
}

inline fun <T> Iterable<T>.sumBy(selector: (T) -> BigInteger): BigInteger {
	var sum = BigInteger.ZERO
	for (element in this) {
		sum += selector(element)
	}
	return sum
}
