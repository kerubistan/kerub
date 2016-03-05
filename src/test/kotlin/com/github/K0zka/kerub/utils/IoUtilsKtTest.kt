package com.github.K0zka.kerub.utils

import org.apache.commons.io.input.NullInputStream
import org.junit.Test

import org.junit.Assert.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Random

class IoUtilsKtTest {

	@Test
	fun copyFromNullInputStream() {
		val byteArrayOutputStream = ByteArrayOutputStream()
		NullInputStream(0).copyTo(byteArrayOutputStream)
		assertEquals(0, byteArrayOutputStream.size())
	}

	@Test
	fun copyFrom() {
		val byteArrayOutputStream = ByteArrayOutputStream()
		NullInputStream(16384).copyTo(byteArrayOutputStream)
		assertEquals(16384, byteArrayOutputStream.size())
	}

	@Test
	fun copyFromRandom() {
		val random = Random()
		val randomBytes = ByteArray(1000000)
		random.nextBytes(randomBytes)
		val byteArrayOutputStream = ByteArrayOutputStream()
		ByteArrayInputStream(randomBytes).copyTo(byteArrayOutputStream)
		assertEquals(randomBytes.size, byteArrayOutputStream.size())
		byteArrayOutputStream.toByteArray().forEachIndexed { i, byte -> assertEquals(byte, randomBytes[i]) }
	}

}