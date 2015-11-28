package com.github.K0zka.kerub.utils

val emptyString = ""

val endOfLine = "\n".toRegex()

fun String.rows() : List<String> =
	this.split(endOfLine)
