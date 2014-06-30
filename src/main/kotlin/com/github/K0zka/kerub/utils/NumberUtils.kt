package com.github.K0zka.kerub.utils

public fun <T : Comparable<T>> T.between(lower: T, higher: T): Boolean =
		this >= lower && this <= higher

