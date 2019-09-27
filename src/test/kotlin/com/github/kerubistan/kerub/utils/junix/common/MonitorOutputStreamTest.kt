package com.github.kerubistan.kerub.utils.junix.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MonitorOutputStreamTest {

	@Test
	fun exceptionTest() {
		class TestException(msg: String) : Exception(msg)
		try {
			MonitorOutputStream(".",  callback = { throw TestException(it) }, parser = { it.toUpperCase() }).use {
				it.write("hello.world.".toByteArray(Charsets.US_ASCII))
			}
		} catch (ex : TestException) {
			assertEquals("HELLO", ex.message)
		}
	}

	@Test
	fun writeByteArray() {
		val result = mutableListOf<String>()
		MonitorOutputStream(".", {result.add(it)}, { it.toUpperCase() }).use {
			it.write("hello.world.".toByteArray(Charsets.US_ASCII))
		}

		assertEquals(listOf("HELLO", "WORLD"), result)
	}

	@Test
	fun writeByteArrayWithOffset() {
		val result = mutableListOf<String>()
		MonitorOutputStream(".", {result.add(it)}, { it.toUpperCase() }).use {
			val byteArray = "hello.world.".toByteArray(Charsets.US_ASCII)
			it.write(byteArray, 0, byteArray.size)
		}

		assertEquals(listOf("HELLO", "WORLD"), result)
	}

	@Test
	fun writeByteOneByOne() {
		val result = mutableListOf<String>()
		MonitorOutputStream(".", {result.add(it)}, { it.toUpperCase() }).use {
			output ->
			"hello.world.".toByteArray(Charsets.US_ASCII).forEach { output.write(it.toInt()) }
		}

		assertEquals(listOf("HELLO", "WORLD"), result)
	}

	@Test
	fun testWriteThroughWriter() {
		val result = mutableListOf<String>()
		MonitorOutputStream(".", {result.add(it)}, { it.toUpperCase() }).writer(Charsets.US_ASCII).use {
			writer ->
			writer.write("hello")
			writer.write(".")
			writer.write("world.")
		}
		assertEquals(listOf("HELLO", "WORLD"), result)
	}
}