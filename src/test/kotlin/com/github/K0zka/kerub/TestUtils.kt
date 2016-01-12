package com.github.K0zka.kerub

import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.fail

fun expect(clazz: KClass<out Exception>, action: () -> Unit) {
	try {
		action()
		fail("expected exception: $clazz")
	} catch (e: Exception) {
		assertEquals(clazz, e.javaClass.kotlin)
	}
}

fun expectException(action: () -> Unit) = expect(Exception::class, action)
