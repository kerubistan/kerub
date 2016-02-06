package com.github.K0zka.kerub.utils

import java.math.BigInteger

fun <T : Comparable<T>> T.between(lower: T, higher: T): Boolean =
		this >= lower && this <= higher

fun Collection<BigInteger>.sum(): BigInteger {
	var sum: BigInteger = BigInteger.ZERO
	this.forEach { sum += it }
	return sum
}