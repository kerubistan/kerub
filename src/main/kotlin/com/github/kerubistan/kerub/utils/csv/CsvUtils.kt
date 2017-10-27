package com.github.kerubistan.kerub.utils.csv

import com.github.kerubistan.kerub.utils.skip

private val coma = ","

fun parseAsCsv(input: String): List<Map<String, String>> =
		input.lines().let {
			lines ->
			val header = lines.first().split(coma)
			val body = lines.skip().filter(String::isNotEmpty)
			body.map {
				line ->
				val fields = line.split(coma)
				fields.mapIndexed { idx, field -> header[idx] to field }.toMap()
			}

		}