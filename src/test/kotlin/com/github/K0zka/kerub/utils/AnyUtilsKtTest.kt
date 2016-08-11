package com.github.K0zka.kerub.utils

import org.junit.Test

import org.junit.Assert.*

class AnyUtilsKtTest {

	@Test
	fun equalsAnyOf() {
		assertTrue("A".equalsAnyOf("A", "B", "C"))
		assertTrue("B".equalsAnyOf("A", "B", "C"))
		assertFalse("D".equalsAnyOf("A", "B", "C"))
		assertTrue(1.equalsAnyOf(1, 2, 3, 4))
	}
}