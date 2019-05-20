package com.github.kerubistan.kerub.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnyUtilsKtTest {

	@Test
	fun equalsAnyOf() {
		assertTrue("A".equalsAnyOf("A", "B", "C"))
		assertTrue("B".equalsAnyOf("A", "B", "C"))
		assertFalse("D".equalsAnyOf("A", "B", "C"))
		assertTrue(1.equalsAnyOf(1, 2, 3, 4))
	}

}