package com.github.kerubistan.kerub.utils

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.slf4j.Logger

class LogUtilsKtTest {

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