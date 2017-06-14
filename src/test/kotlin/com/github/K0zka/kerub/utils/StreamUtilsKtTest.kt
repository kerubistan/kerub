package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.expect
import org.junit.Assert.assertEquals
import org.junit.Test

class StreamUtilsKtTest {

	@Test
	fun resourceToString() {
		val data = resourceToString("com/github/K0zka/kerub/utils/urlToStringTest.txt")
		assertEquals("Hello World!", data)
	}

	@Test
	fun resourceToStringNotExisting() {
		expect(IllegalArgumentException::class) { resourceToString("notexisting-" + System.currentTimeMillis()) }
	}

}