package com.github.K0zka.kerub.utils

import org.junit.Test
import kotlin.test.assertEquals

public class MapUtilsTest {
	Test
	fun plus() {
		assertEquals(mapOf("1" to 1, "2" to 2), mapOf("1" to 1).plus(mapOf("2" to 2)))
	}
	Test
	fun plusElem() {
		assertEquals(mapOf("1" to 1, "2" to 2), mapOf("1" to 1).plus("2" to 2))
	}
	Test
	fun toPairList() {
		assertEquals( listOf(1 to 2), listOf(1 to 2) )
	}
}