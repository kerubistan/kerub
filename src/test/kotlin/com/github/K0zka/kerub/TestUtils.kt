package com.github.K0zka.kerub

import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

fun expect(clazz: KClass<out Exception>, action: () -> Unit) {
	expect(clazz = clazz, action = action, check = {
		assertTrue(clazz == it?.javaClass?.kotlin)
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
			fail("expected $clazz got $e")
		}
	}
}
