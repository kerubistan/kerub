package com.github.kerubistan.kerub.utils

import com.github.kerubistan.kerub.expect
import org.junit.Assert.assertEquals
import org.junit.Test

class StreamUtilsKtTest {

	@Test
	fun resourceToString() {
		val data = resourceToString("com/github/kerubistan/kerub/utils/urlToStringTest.txt")
		assertEquals("Hello World!", data)
	}

	@Test
	fun resourceToStringNotExisting() {
		expect(IllegalArgumentException::class) { resourceToString("notexisting-" + System.currentTimeMillis()) }
	}

}