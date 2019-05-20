package com.github.kerubistan.kerub.utils

import java.math.BigInteger

const val emptyString = ""

private val duplicateSlashesPattern = "(/+)".toRegex()

fun String.toBigInteger() = BigInteger(this)

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