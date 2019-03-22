package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import org.junit.Assert.assertEquals
import org.junit.Test

class SoftwarePackageTest {
	@Test
	fun testToString() {
		assertEquals("awesomeshit-1.2.3",pack("awesomeshit","1.2.3").toString())
	}
}