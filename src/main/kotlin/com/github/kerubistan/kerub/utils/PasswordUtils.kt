package com.github.kerubistan.kerub.utils

import java.nio.charset.Charset
import java.util.Base64
import java.util.Random

private val pwdCharacters = (charsBetween('a', 'z') +
		charsBetween('A', 'Z') +
		charsBetween('0', '9') +
		listOf('_', '-', '.')).toCharArray()

private val random = Random()

private fun charsBetween(start: Char, end: Char): List<Char> {
	return CharArray(end - start, { start + it }).toList()
}

fun genPassword(length: Int = 16): String {
	return buildString(length) {
		for (i in 1..length) {
			append(pwdCharacters[random.nextInt(pwdCharacters.size - 1)])
		}
	}
}

fun String.base64(charset: Charset = Charsets.UTF_8) = Base64.getEncoder().encode(this.toByteArray(charset))

fun ByteArray.base64decode(charset: Charset = Charsets.UTF_8) = Base64.getDecoder().decode(this).toString(charset)
