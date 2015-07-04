package com.github.K0zka.kerub

import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.verification.VerificationMode
import kotlin.reflect.KClass
import kotlin.reflect.jvm.java

fun verify<T>(param : T) = Mockito.verify(param)

fun verify<T>(param : T, mode : VerificationMode) = Mockito.verify(param, mode)

val once = Mockito.only()

val never = Mockito.never()

fun times(n : Int) = Mockito.times(n)

fun on<T>(param : T) = Mockito.`when`(param)

fun eq<T>(param : T) = Matchers.eq(param) ?: param

fun anyString() = Matchers.anyString() ?: ""

fun anyInt() = Matchers.anyInt() ?: 1

fun anyLong() = Matchers.anyLong() ?: 1L

fun matchAny<T>(clazz : Class<T>) : T = Matchers.any(clazz) ?: Mockito.mock(clazz)

fun matchAny<T>(clazz : Class<T>, instance : T) : T = Matchers.any(clazz) ?: instance
