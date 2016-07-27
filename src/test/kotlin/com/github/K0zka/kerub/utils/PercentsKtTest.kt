package com.github.K0zka.kerub.utils

import org.junit.Test

import org.junit.Assert.*

class PercentsKtTest {

	@Test
	fun asPercentOf() {
		(0..200).forEach {
			assertEquals(" $it ", it, it.asPercentOf(100))
		}
		assertEquals(10, 1.asPercentOf(10))
		assertEquals(20, 2.asPercentOf(10))
		assertEquals(10, 25.asPercentOf(250))
		assertEquals(20, 2.asPercentOf(10))
	}
}