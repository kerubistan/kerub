package com.github.K0zka.kerub

import org.mockito.Matchers
import org.mockito.Mockito
import org.mockito.verification.VerificationMode
import kotlin.reflect.KClass

fun <T> verify(param : T) = Mockito.verify(param)

fun <T> verify(param : T, mode : VerificationMode) = Mockito.verify(param, mode)

val once = Mockito.only()

val never = Mockito.never()

fun times(n : Int) = Mockito.times(n)

fun <T> on(param : T) = Mockito.`when`(param)

fun <T> eq(param : T) = Matchers.eq(param) ?: param

fun anyString() = Matchers.anyString() ?: ""

fun anyInt() = Matchers.anyInt()

fun anyLong() = Matchers.anyLong()

fun <T> matchAny(clazz : Class<T>) : T = Matchers.any(clazz) ?: Mockito.mock(clazz)
fun <T : Any> matchAny(clazz : KClass<T>) : T = matchAny(clazz.java)

fun <T> matchAny(clazz : Class<T>, instance : T) : T = Matchers.any(clazz) ?: instance
