package com.github.K0zka.kerub.utils

import java.math.BigInteger
import java.util.UUID

val emptyString = ""

val endOfLine = "\n".toRegex()

fun String.rows(): List<String> =
		this.split(endOfLine)

fun String.toUUID(): UUID =
		UUID.fromString(this)

fun String.toBigInteger() = BigInteger(this)

fun String.remove(regex: Regex)
		= this.replace(regex, "")

fun String.substringBetween(prefix: String, postfix: String): String =
		this.substringAfter(prefix, "").substringBefore(postfix, "")

fun String.substringBetweenOrNull(prefix: String, postfix: String): String? =
		this.substringAfterOrNull(prefix)?.substringBeforeOrNull(postfix)

fun <T> String.doWithDelimiter(delimiter: String, action: (idx: Int) -> T): T? {
	val idx = this.indexOf(delimiter)
	return if (idx < 0) null else action(idx)
}

fun String.substringAfterOrNull(delimiter: String): String? = doWithDelimiter(delimiter) { this.substring(it + 1, this.length) }

fun String.substringBeforeOrNull(delimiter: String): String? = doWithDelimiter(delimiter) { this.substring(0, it) }

