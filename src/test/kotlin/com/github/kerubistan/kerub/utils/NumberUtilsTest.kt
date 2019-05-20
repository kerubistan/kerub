package com.github.kerubistan.kerub.utils

import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.KB
import io.github.kerubistan.kroki.size.MB
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberUtilsTest {
	@Test
	fun compareWithBetween() {
		assertTrue("B".between("A", "C"))
		assertTrue(1.between(0, 2))
		assertTrue(0.1.between(0.09, 0.11))
	}

	@Test
	fun compareWithIn() {
		assertTrue(1 in 0..2)
		assertTrue(0.1 in 0.09..0.11)
	}

	@Test
	fun decimalSumBy() {
		assertEquals(
				BigDecimal("2.555"),
				listOf(BigDecimal("1.1"), BigDecimal("1.45"), BigDecimal("0.005"))
						.decimalSumBy { it }
		)
		assertEquals(
				BigDecimal.ZERO,
				listOf<BigDecimal>()
						.decimalSumBy { it }
		)
	}

	@Test
	fun decimalAvgBy() {
		assertEquals(
				BigDecimal("1.1"),
				listOf(BigDecimal("1.1"), BigDecimal("1.0"), BigDecimal("1.2"))
						.decimalAvgBy { it }
		)
		assertEquals(
				BigDecimal("1.5"),
				listOf(BigDecimal("1"), BigDecimal("2"))
						.decimalAvgBy { it }
		)
		assertEquals(
				BigDecimal.ZERO,
				listOf<BigDecimal>()
						.decimalAvgBy { it }
		)
	}

	@Test
	fun roundUp() {
		assertEquals(1.GB, 1.KB.roundUp(unit = 1.KB, minimum = 1.GB))
		assertEquals(128.KB, 1.KB.roundUp(unit = 128.KB))
		assertEquals(256.KB, 129.KB.roundUp(unit = 128.KB))
		assertEquals(512.KB, 129.KB.roundUp(unit = 128.KB, minimum = 512.KB))
		assertEquals(4.MB, 0.KB.roundUp(unit = 1.KB, minimum = 4.MB))
	}

}