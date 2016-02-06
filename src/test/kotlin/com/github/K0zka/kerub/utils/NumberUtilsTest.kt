package com.github.K0zka.kerub.utils

import org.junit.Test
import kotlin.test.assertTrue

class NumberUtilsTest {
	@Test
	fun compareWithBetween() {
		assertTrue("B".between("A", "C"))
		assertTrue(1.between(0, 2))
		assertTrue(0.1.between(0.09, 0.11))
	}
	@Test
	fun compareWithIn() {
		assertTrue(1 in 0..2)
		assertTrue(0.1 in 0.09..0.11)
	}
}