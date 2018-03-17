package com.github.kerubistan.kerub.utils

fun Boolean.flag(trueStr: String, falseStr: String = ""): String =
		if (this) {
			trueStr
		} else {
			falseStr
		}
