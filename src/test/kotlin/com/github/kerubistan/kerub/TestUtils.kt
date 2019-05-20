package com.github.kerubistan.kerub

import com.github.kerubistan.kerub.utils.getLogger
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.test.fail

val logger = getLogger("test-utils")

fun <T : Exception> expect(message : String? = null, clazz: KClass<T>, action: () -> Unit, check: (T) -> Unit) {
	try {
		action()
		fail("${message ?: "" } \nexpected exception: $clazz")
	} catch (e: Exception) {
		if (clazz == e.javaClass.kotlin) {
			check(e as T)
		} else {
			logger.error(e.message, e)
			fail("expected $clazz got $e")
		}
	}
}

fun <T : Exception> expect(clazz: KClass<T>, action: () -> Unit, check: (T) -> Unit) {
	expect(null, clazz, action, check)
}

fun String.toInputStream(charset: Charset = Charsets.UTF_8): InputStream
		= ByteArrayInputStream(this.toByteArray(charset))
