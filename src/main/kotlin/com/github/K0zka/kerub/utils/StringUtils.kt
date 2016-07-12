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
