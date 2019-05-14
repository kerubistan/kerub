package com.github.kerubistan.kerub.utils

import java.math.BigInteger
import java.util.UUID

const val emptyString = ""

// moved to kroki-utils
private val uuidPattern = "([0-9]|[a-f]){8}-([0-9]|[a-f]){4}-([0-9]|[a-f]){4}-([0-9]|[a-f]){4}-([0-9]|[a-f]){12}"
		.toRegex()

private val duplicateSlashesPattern = "(/+)".toRegex()

// moved to kroki-utils
fun String.isUUID() = this.matches(uuidPattern)

// moved to kroki-utils
fun String.toUUID(): UUID =
		UUID.fromString(this)

fun String.toBigInteger() = BigInteger(this)

fun String.remove(regex: Regex)
		= this.replace(regex, "")

// moved to kroki
fun String.substringBetween(prefix: String, postfix: String): String =
		this.substringAfter(prefix, "").substringBefore(postfix, "")

fun String.substringBetweenOrNull(prefix: String, postfix: String): String? =
		this.substringAfterOrNull(prefix)?.substringBeforeOrNull(postfix)

fun <T> String.doWithDelimiter(delimiter: String, action: (idx: Int) -> T): T? {
	val idx = this.indexOf(delimiter)
	return if (idx < 0) null else action(idx)
}

fun String.substringAfterOrNull(delimiter: String): String?
		= doWithDelimiter(delimiter) {
	this.substring(it + delimiter.length, this.length)
}

fun String.substringBeforeOrNull(delimiter: String): String?
		= doWithDelimiter(delimiter) {
	this.substring(0, it)
}

fun String.normalizePath() : String = this.replace(duplicateSlashesPattern, "/")