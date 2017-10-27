package com.github.kerubistan.kerub.utils

fun Boolean.flag(trueStr: String): String =
		if (this) {
			trueStr
		} else {
			""
		}
