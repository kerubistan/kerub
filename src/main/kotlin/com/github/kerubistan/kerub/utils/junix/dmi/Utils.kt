package com.github.kerubistan.kerub.utils.junix.dmi

import io.github.kerubistan.kroki.strings.substringBetween
import java.math.BigDecimal


fun String.bigDecimalBetween(prefix: String, postfix: String): BigDecimal? {
	try {
		return BigDecimal(this.substringAfter(prefix, "").substringBefore(postfix, ""))
	} catch (nfe: NumberFormatException) {
		return null
	}
}


fun String.intBetween(prefix: String, postfix: String): Int =
		this.substringAfter(prefix).substringBefore(postfix).toInt()

fun String.optionalIntBetween(prefix: String, postfix: String): Int? {
	try {
		return this.substringBetween(prefix, postfix).trim().toInt()
	} catch (nfe: NumberFormatException) {
		return null
	}
}
