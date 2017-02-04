package com.github.K0zka.kerub.utils.csv

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvUtilsKtTest {
	@Test
	fun testParseAsCsv() {
		assertEquals(listOf(mapOf("name" to "bob", "id" to "1"), mapOf("name" to "john", "id" to "2")), parseAsCsv(
				"""name,id
bob,1
john,2
"""
		))
	}

}