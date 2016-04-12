package com.github.K0zka.kerub.utils

import org.junit.Test
import kotlin.test.assertNull

class ExceptionUtilsTest {
	@Test
	fun silentFail() {
		assertNull(silent { throw NullPointerException() })
	}

	@Test
	fun silentPass() {
		assert(silent { "OK" } == "OK")
	}

	@Test(expected = IllegalArgumentException::class)
	fun insistAndFail() {
		insist(3) {
			throw IllegalArgumentException("TEST")
		}
	}

	@Test
	fun insistAndPass() {
		var cntr = 0
		insist(3) {
			require(cntr++ > 2)
		}
		assert(cntr > 2)
	}

}

