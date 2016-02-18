package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.expect
import org.junit.Test
import kotlin.test.assertEquals

class ListUtilsTest {

	@Test
	fun only() {
		assertEquals("A", listOf("A").only())
		expect(IllegalArgumentException::class, {listOf<String>().only()})
		expect(IllegalArgumentException::class, {listOf("A","B").only()})
	}

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