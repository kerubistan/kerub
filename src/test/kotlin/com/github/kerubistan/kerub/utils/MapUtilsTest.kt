package com.github.kerubistan.kerub.utils

import org.junit.Test
import kotlin.test.assertEquals

class MapUtilsTest {

	@Test
	fun toPairList() {
		assertEquals(listOf(1 to 2), listOf(1 to 2))
	}

	@Test
	fun mapInverse() {
		assertEquals(
				mapOf(1 to "one", 2 to "two", 3 to "three"),
				mapOf("one" to 1, "two" to 2, "three" to 3).inverse()
		)
		assertEquals(
				mapOf(),
				mapOf<String, String>().inverse()
		)

	}
}