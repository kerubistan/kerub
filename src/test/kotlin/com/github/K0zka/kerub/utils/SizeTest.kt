package com.github.K0zka.kerub.utils

import org.junit.Assert
import org.junit.Test

public class SizeTest {

	@Test
	fun parseStrorageSize() {
		Assert.assertEquals(512, parseStorageSize("512 B").intValue())
		Assert.assertEquals(512, parseStorageSize("0.5 KB").intValue())
		Assert.assertEquals(256, parseStorageSize("0.25 KB").intValue())
		Assert.assertEquals(1024, parseStorageSize("1 KB").intValue())
		Assert.assertEquals(1024, parseStorageSize("1.0 KB").intValue())
		Assert.assertEquals(1024, parseStorageSize("1.0 kB").intValue())
		Assert.assertEquals(1024, parseStorageSize("1 kB").intValue())

		Assert.assertEquals(1024 * 1024, parseStorageSize("1 mB").intValue())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1 MB").intValue())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.0 MB").intValue())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.00 MB").intValue())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1.0 mB").intValue())
		Assert.assertEquals(1024 * 1024, parseStorageSize("1 mb").intValue())

		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1 gB").intValue())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1 GB").intValue())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1.0 GB").intValue())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1.00 GB").intValue())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1.0 GB").intValue())
		Assert.assertEquals(1024 * 1024 * 1024, parseStorageSize("1 gb").intValue())

	}

}