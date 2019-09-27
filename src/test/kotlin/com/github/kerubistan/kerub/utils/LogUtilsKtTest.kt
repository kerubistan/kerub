package com.github.kerubistan.kerub.utils

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.slf4j.Logger
import kotlin.test.fail

class LogUtilsKtTest {

	@Test
	fun doOrLogAndDie() {
		class TestException : Exception()
		val logger = mock<Logger>()
		whenever(logger.isWarnEnabled).thenReturn(true)

		try {
			logger.doOrLog("exception: %s", "test-data") {
				throw TestException()
			}
			fail("exception expected")
		} catch (exc : TestException) {
			//expected
		}
		verify(logger).warn(eq("exception: test-data"))
	}

	@Test
	fun doOrLog() {
		val logger = mock<Logger>()
		whenever(logger.isDebugEnabled).thenReturn(true)

		logger.doOrLog("exception: %s", "test-data") {
			// do actually nothing
		}
		verify(logger, never()).warn(any())
	}

	@Test
	fun debugLazy() {
		val logger = mock<Logger>()
		whenever(logger.isDebugEnabled).thenReturn(true)
		val lazy = mock<Lazy<*>>()
		whenever(lazy.value).thenReturn("ok")

		logger.debugLazy("test", lazy)

		verify(lazy).value
	}

	@Test
	fun debugLazyDisabled() {
		val logger = mock<Logger>()
		whenever(logger.isDebugEnabled).thenReturn(false)
		val lazy = mock<Lazy<*>>()
		whenever(lazy.value).thenReturn("ok")

		logger.debugLazy("test", lazy)

		verify(lazy, never()).value
	}

}