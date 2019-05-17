package com.github.kerubistan.kerub.utils

import io.github.kerubistan.kroki.time.now
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class StreamUtilsKtTest {

	@Test
	fun resourceToString() {
		val data = resourceToString("com/github/kerubistan/kerub/utils/urlToStringTest.txt")
		assertEquals("Hello World!", data)
	}

	@Test
	fun resourceToStringNotExisting() {
		assertThrows<IllegalArgumentException> { resourceToString("notexisting-${now()}") }
	}

}