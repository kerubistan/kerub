package com.github.K0zka.kerub.utils

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext

operator fun BigDecimal.div(divider: Int) = this / BigDecimal(divider)

operator fun BigDecimal.div(divider: Double) = this / BigDecimal(divider)

operator fun BigInteger.div(divider: Int) = BigDecimal(this) / BigDecimal.valueOf(divider.toLong())

operator fun BigInteger.div(divider: BigDecimal) = BigDecimal(this) / divider

operator fun BigInteger.div(divider: Double) = this / BigDecimal(divider.toString())

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

inline fun <T> Iterable<T>.decimalSumBy(selector: (T) -> BigDecimal): BigDecimal {
	var sum = BigDecimal.ZERO
	for (element in this) {
		sum += selector(element)
	}
	return sum
}

fun <T> Iterable<T>.decimalAvgBy(selector: (T) -> BigDecimal): BigDecimal {
	var sum = BigDecimal.ZERO
	var cnt = 0
	for (element in this) {
		sum += selector(element)
		cnt++
	}
	return if (cnt > 0) {
		//TODO this is workaround for kotlin 1.1.4-2
		// this is very likely wrong in kotlin, but sum / cnt it is rounding up
		// therefore BigDecimal("3") / BigDecimal("2") will evaluate to 2
		// see testcase NumberUtilsTest.decimalAvgBy for verification
		sum.divide(BigDecimal(cnt.toString()), MathContext.DECIMAL32)
	} else {
		//we say that the average of no elements is zero... is that actually so?
		BigDecimal.ZERO
	}
}

/**
 * Whatever we get in a numeric property change, let's just make a BigDecimal from it
 */
fun bd(something : Any?) =
		something?.let {
			when(it) {
				is BigDecimal -> it
				is BigInteger -> BigDecimal(it)
				is Long -> BigDecimal(it)
				is Double -> BigDecimal(it)
				is Float -> BigDecimal(it.toDouble())
				is Int -> BigDecimal(it)
				is Byte -> BigDecimal(it.toInt())
				else ->
					BigDecimal(it.toString())
			}
		}
