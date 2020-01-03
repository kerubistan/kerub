package com.github.kerubistan.kerub.utils

import java.nio.charset.Charset
import java.util.Base64
import java.util.Random

private val pwdCharacters = (
		('a'..'z').toList() +
				('A'..'Z').toList() +
				('0'..'9').toList() +
				listOf('_', '-', '.')).toCharArray()

private val random = Random()

// moved to kroki
fun genPassword(length: Int = 16, rnd: Random = random) = buildString(length) {
	repeat(length) {
		append(pwdCharacters[rnd.nextInt(pwdCharacters.size - 1)])
	}
}

// move to kroki
fun String.base64(charset: Charset = Charsets.UTF_8) = Base64.getEncoder().encode(this.toByteArray(charset))

// move to kroki
fun ByteArray.base64decode(charset: Charset = Charsets.UTF_8) = Base64.getDecoder().decode(this).toString(charset)
