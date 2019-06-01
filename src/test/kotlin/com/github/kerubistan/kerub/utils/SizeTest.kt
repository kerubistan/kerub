package com.github.kerubistan.kerub.utils

import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger
import kotlin.test.assertEquals

class SizeTest {

	@Test
	fun parseStrorageSize() {
		assertEquals(512, parseStorageSize("512 B").toInt())
		assertEquals(512, parseStorageSize("512 byte").toInt())
		assertEquals(512, parseStorageSize("512 bytes").toInt())
		assertEquals(512, parseStorageSize("0.5 KB").toInt())
		assertEquals(256, parseStorageSize("0.25 KB").toInt())
		assertEquals(1024, parseStorageSize("1 KB").toInt())
		assertEquals(1024, parseStorageSize("1.0 KB").toInt())
		assertEquals(1024, parseStorageSize("1.0 kB").toInt())
		assertEquals(1024, parseStorageSize("1 kB").toInt())

		assertEquals(1024 * 1024, parseStorageSize("1 mB").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1 MB").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1 M").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1M").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1.0M").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1 m").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1m").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1.0m").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1.0 MB").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1.00 MB").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1.0 mB").toInt())
		assertEquals(1024 * 1024, parseStorageSize("1 mb").toInt())

		assertEquals(1024 * 1024 * 1024, parseStorageSize("1 gB").toInt())
		assertEquals(1024 * 1024 * 1024, parseStorageSize("1 GB").toInt())
		assertEquals(1024 * 1024 * 1024, parseStorageSize("1G").toInt())
		assertEquals(1024 * 1024 * 1024, parseStorageSize("1.0 GB").toInt())
		assertEquals(1024 * 1024 * 1024, parseStorageSize("1.00 GB").toInt())
		assertEquals(1024 * 1024 * 1024, parseStorageSize("1.0 GB").toInt())
		assertEquals(1024 * 1024 * 1024, parseStorageSize("1 gb").toInt())

		assertEquals(1024.toLong() * 1024 * 1024 * 1024, parseStorageSize("1 T").toLong())
		assertEquals(1024.toLong() * 1024 * 1024 * 1024, parseStorageSize("1 TB").toLong())

		assertEquals(1024.toLong() * 1024 * 1024 * 1024 * 1024, parseStorageSize("1 P").toLong())
		assertEquals(1024.toLong() * 1024 * 1024 * 1024 * 1024, parseStorageSize("1 PB").toLong())

		assertEquals(65536, parseStorageSize("64K").toInt())
		assertEquals(65536, parseStorageSize("64 K").toInt())

	}

	@Test
	fun validateSize() {
		assertThrows<IllegalStateException> {
			BigInteger("-1").validateSize("test")
		}
		BigInteger("0").validateSize("test")
		BigInteger("1").validateSize("test")
	}

}