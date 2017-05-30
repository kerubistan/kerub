package com.github.K0zka.kerub.model.history

import org.junit.Assert
import org.junit.Test

class HistoryKeyTest {
	@Test
	fun testToString() {
		Assert.assertTrue(HistoryKey("foo").toString().startsWith("foo - "))
		Assert.assertTrue(HistoryKey("foo", 0).toString().startsWith("foo - 19"))
	}

	@Test
	fun testEquals() {
		Assert.assertEquals(HistoryKey("foo", 0), HistoryKey("foo", 0))
		Assert.assertNotEquals(HistoryKey("foo", 0), HistoryKey("Bar", 0))
		Assert.assertNotEquals(HistoryKey("foo", 0), null)
		Assert.assertNotEquals(HistoryKey("foo", 0), HistoryKey("foo", 1))
		Assert.assertNotEquals(HistoryKey("foo", 0), HistoryKey(4, 0))
	}

	@Test
	fun testHashCode() {
		Assert.assertEquals(HistoryKey("foo", 0).hashCode(), HistoryKey("foo", 0).hashCode())
	}
}