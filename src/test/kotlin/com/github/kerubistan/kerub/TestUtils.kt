package com.github.kerubistan.kerub

import com.github.kerubistan.kerub.utils.getLogger
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigInteger
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.test.assertTrue
import kotlin.test.fail

val logger = getLogger("test-utils")

fun expect(clazz: KClass<out Exception>, action: () -> Unit) {
	expect(clazz = clazz, action = action, check = {
		assertTrue(clazz == it.javaClass.kotlin)
	})
}

fun <T : Exception> expect(clazz: KClass<T>, action: () -> Unit, check: (T) -> Unit) {
	try {
		action()
		fail("expected exception: $clazz")
	} catch (e: Exception) {
		if (clazz == e.javaClass.kotlin) {
			check(e as T)
		} else {
			logger.error(e.message, e)
			fail("expected $clazz got $e")
		}
	}
}

fun String.toInputStream(charset: Charset = Charsets.UTF_8): InputStream
		= ByteArrayInputStream(this.toByteArray(charset))

private val K = 1024.toBigInteger()

val Int.KB: BigInteger
	get() = this.toBigInteger() * K

val Int.MB: BigInteger
	get() = this.KB * K

val Int.GB: BigInteger
	get() = this.MB * K

val Int.TB: BigInteger
	get() = this.GB * K

val Int.PB: BigInteger
	get() = this.TB * K
