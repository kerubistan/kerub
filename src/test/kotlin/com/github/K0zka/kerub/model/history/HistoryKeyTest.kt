package com.github.K0zka.kerub.model.history

import org.junit.Test
import org.junit.Assert

public class HistoryKeyTest {
	Test
	fun testToString() {
		Assert.assertTrue(HistoryKey<String>("foo").toString().startsWith("foo - "))
		Assert.assertTrue(HistoryKey<String>("foo", 0).toString().startsWith("foo - 19"))
	}

	Test
	fun testEquals() {
		Assert.assertEquals( HistoryKey<String>("foo", 0), HistoryKey<String>("foo", 0) )
		Assert.assertNotEquals( HistoryKey<String>("foo", 0), HistoryKey<String>("Bar", 0) )
		Assert.assertNotEquals( HistoryKey<String>("foo", 0), null )
		Assert.assertNotEquals( HistoryKey<String>("foo", 0), HistoryKey<String>("foo", 1) )
		Assert.assertNotEquals( HistoryKey<String>("foo", 0), HistoryKey<Int>(4, 0) )
	}

	Test
	fun testHashCode() {
		Assert.assertEquals(HistoryKey<String>("foo", 0).hashCode(), HistoryKey<String>("foo", 0).hashCode());
	}
}