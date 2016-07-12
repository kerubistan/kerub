package com.github.K0zka.kerub.utils

import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class StringUtilsTest {

	val str = """bla
bla bla
bla bla bla"""

	@Test
	fun rows() {
		val rows = str.rows()
		assertEquals(3, rows.size)
		assertEquals("bla", rows[0])
		assertEquals("bla bla", rows[1])
		assertEquals("bla bla bla", rows[2])
	}

	@Test
	fun toBigInteger() {
		assertEquals(BigInteger.ONE, "1".toBigInteger())
		assertEquals(BigInteger.TEN, "10".toBigInteger())
	}

	@Test
	fun remove() {
		assertEquals("foo bar baz", "foo, bar, baz".remove(",".toRegex()))
		assertEquals("abcdefgh", "abc12def45gh6".remove("\\d+".toRegex()))
		assertEquals("123456", "12   3  45\t6".remove("\\s+".toRegex()))
	}
}