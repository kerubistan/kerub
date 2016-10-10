package com.github.K0zka.kerub.utils

fun Boolean.flag(trueStr: String): String =
		if (this) {
			trueStr
		} else {
			""
		}
