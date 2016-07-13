package com.github.K0zka.kerub.utils

import java.math.BigDecimal
import java.math.BigInteger

val sizeMultiplier = BigDecimal("1024");

val KB = sizeMultiplier
val MB = KB * sizeMultiplier
val GB = MB * sizeMultiplier
val TB = GB * sizeMultiplier
val PB = TB * sizeMultiplier

val sizePostfixes = mapOf<String, (BigDecimal) -> BigDecimal>(
		"BYTES" to { l: BigDecimal -> l },
		"BYTE" to { l: BigDecimal -> l },
		"B" to { l: BigDecimal -> l },
		"KB" to { l: BigDecimal -> l * KB },
		"MB" to { l: BigDecimal -> l * MB },
		"GB" to { l: BigDecimal -> l * GB },
		"TB" to { l: BigDecimal -> l * TB },
		"PB" to { l: BigDecimal -> l * PB })

val numberRegex = "\\d+(\\.\\d+)?".toRegex()

fun parseStorageSize(storageSize: String): BigInteger {
	val unit = storageSize.replace(numberRegex, "").trim()
	val num = BigDecimal(storageSize.substringBefore(unit).trim())
	val fn = requireNotNull(sizePostfixes.get(unit.toUpperCase()), { "Unknown storage unit ${unit} in ${storageSize}" })
	return fn(num).toBigInteger()
}

fun String.toSize() =
		parseStorageSize(this)