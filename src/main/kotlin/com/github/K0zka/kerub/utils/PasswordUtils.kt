package com.github.K0zka.kerub.utils

import java.util.Random

private val pwdCharacters = (charsBetween('a', 'z') +
		charsBetween('A', 'Z') +
		charsBetween('0', '9') +
		listOf('_', '-', '.')).toCharArray()

private val random = Random()

private fun charsBetween(start : Char, end : Char) : List<Char> {
	return CharArray(end - start, {start + it}).toList()
}

//TODO: candidate to contribution to kotlin - https://github.com/kerubistan/kerub/issues/137
inline fun buildString(length : Int, builderAction: StringBuilder.() -> Unit): String = StringBuilder().apply(builderAction).toString()

fun genPassword(length : Int = 16) : String {
	return buildString(length) {
		for(i in 1 .. length) {
			append(pwdCharacters[random.nextInt(pwdCharacters.size - 1)])
		}
	}
}