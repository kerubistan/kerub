package com.github.K0zka.kerub.host

import org.junit.Test
import kotlin.test.assertEquals

class ByteArrayUtilsTest {
	@Test
	fun toBase64() {
		val array = ByteArray(4)
		array[0] = 0
		array[1] = 1
		array[2] = 2
		array[3] = 3
		assertEquals("AAECAw==", array.toBase64())
	}
}