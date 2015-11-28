package com.github.K0zka.kerub.utils

import org.junit.Assert
import org.junit.Test

class StringUtilsTest {

	val str = """bla
bla bla
bla bla bla"""

	@Test
	fun rows() {
		val rows = str.rows()
		Assert.assertEquals(3, rows.size)
		Assert.assertEquals("bla", rows[0])
		Assert.assertEquals("bla bla", rows[1])
		Assert.assertEquals("bla bla bla", rows[2])
	}

}