package com.github.kerubistan.kerub.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PasswordUtilsKtTest {

	@Test
	fun base64() {
		assertNotEquals("hello world", "hello world".base64().toString(Charsets.UTF_8))
		assertEquals("","".base64().base64decode())
		assertEquals("hello world","hello world".base64().base64decode())
	}

}
