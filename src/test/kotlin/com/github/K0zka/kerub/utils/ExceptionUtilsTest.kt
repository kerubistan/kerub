package com.github.K0zka.kerub.utils

import org.junit.Test

class ExceptionUtilsTest {
	@Test
	fun silentFail() {
		assert( silent { throw NullPointerException() } == null )
	}
	@Test
	fun silentPass() {
		assert( silent { "OK" } == "OK" )
	}
}