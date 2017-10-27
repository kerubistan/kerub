package com.github.kerubistan.kerub.utils

import org.junit.Assert
import org.junit.Test

class SizeTest {

	@Test
	fun parseStrorageSize() {
		Assert.assertEquals(512, parseStorageSize("512 B").toInt())
		Assert.assertEquals(512, parseStorageSize("512 byte").toInt())
		Assert.assertEquals(512, parseStorageSize("512 bytes").toInt())
		Assert.assertEquals(512, parseStorageSize("0.5 KB").toInt())
		Assert.assertEquals(256, parseStorageSize("0.25 KB").toInt())
		Assert.assertEquals(1024, parseStorageSize("1 KB").toInt())
		Assert.assertEquals(1024, parseStorageSize("1.0 KB").toInt())
		Assert.assertEquals(1024, parseStorageSize("1.0 kB").toInt())
		Assert.assertEquals(1024, parseStorageSize("1 kB").toInt())

		Assert.assertEquals(1024 * 1024, parseStorageSize("1 mB").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1 MB").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1 M").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1M").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.0M").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1 m").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1m").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.0m").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.0 MB").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.00 MB").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.0 mB").toInt())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1 mb").toInt())

		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1 gB").toInt())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1 GB").toInt())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1G").toInt())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1.0 GB").toInt())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1.00 GB").toInt())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1.0 GB").toInt())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1 gb").toInt())

		Assert.assertEquals(1024.toLong() * 1024 * 1024 * 1024, parseStorageSize("1 T").toLong())
		Assert.assertEquals(1024.toLong() * 1024 * 1024 * 1024, parseStorageSize("1 TB").toLong())

		Assert.assertEquals(1024.toLong() * 1024 * 1024 * 1024 * 1024, parseStorageSize("1 P").toLong())
		Assert.assertEquals(1024.toLong() * 1024 * 1024 * 1024 * 1024, parseStorageSize("1 PB").toLong())

	}

}