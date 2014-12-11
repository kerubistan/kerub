package com.github.K0zka.kerub

import org.mockito.Matchers
import org.mockito.Mockito

fun matchAny<T>(clazz : Class<T>) : T {
	return Matchers.any(clazz) ?: Mockito.mock(clazz)
}

fun matchAny<T>(clazz : Class<T>, instance : T) : T {
	return Matchers.any(clazz) ?: instance
}