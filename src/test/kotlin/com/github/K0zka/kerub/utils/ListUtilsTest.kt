package com.github.K0zka.kerub.utils

import org.junit.Assert
import org.junit.Test

class ListUtilsTest {
	@Test
	fun skip() {
		Assert.assertEquals(listOf("B", "C"), listOf("A", "B", "C").skip())
		Assert.assertEquals(listOf("C"), listOf("A", "B", "C").skip().skip())
		Assert.assertEquals(listOf<String>(), listOf<String>().skip())
		Assert.assertEquals(listOf<String>(), listOf("A").skip())
	}
}