package com.github.K0zka.kerub

import org.mockito.Matchers
import org.mockito.Mockito
import kotlin.reflect.KClass
import kotlin.reflect.jvm.java

fun verify<T>(param : T) = Mockito.verify(param)

fun on<T>(param : T) = Mockito.`when`(param)

fun eq<T>(param : T) = Matchers.eq(param) ?: param

fun anyString() = Matchers.anyString() ?: ""

fun anyInt() = Matchers.anyInt() ?: 1

fun anyLong() = Matchers.anyLong() ?: 1L

fun matchAny<T>(clazz : Class<T>) : T = Matchers.any(clazz) ?: Mockito.mock(clazz)

fun matchAny<T>(clazz : Class<T>, instance : T) : T = Matchers.any(clazz) ?: instance
