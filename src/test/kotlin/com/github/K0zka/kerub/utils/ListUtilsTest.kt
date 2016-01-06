package com.github.K0zka.kerub.utils

import org.junit.Test
import kotlin.test.assertEquals

class ListUtilsTest {
	@Test
	fun skip() {
		assertEquals(listOf("B", "C"), listOf("A", "B", "C").skip())
		assertEquals(listOf("C"), listOf("A", "B", "C").skip().skip())
		assertEquals(listOf<String>(), listOf<String>().skip())
		assertEquals(listOf<String>(), listOf("A").skip())
	}

	@Test
	fun join() {
		assertEquals(listOf("A", "B", "C", "D"), listOf(listOf("A"), listOf("B", "C", "D")).join())
		assertEquals(listOf("A", "B", "C", "D"), listOf(listOf("A", "B"), listOf("C", "D")).join())
		assertEquals(listOf<String>(), listOf(listOf<String>(), listOf<String>()).join())
	}
}