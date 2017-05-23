package com.github.K0zka.kerub.utils.countdown

import com.github.K0zka.kerub.expect
import com.nhaarman.mockito_kotlin.doCallRealMethod
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeoutException

class TimerTest {
	@Test
	fun check() {
		val timer = spy(Timer(limit = 1000, start = 0))
		doCallRealMethod().`when`(timer).check()
		doReturn(600.toLong()).`when`(timer).now()
		assertEquals(400, timer.check())
	}

	@Test
	fun checkTimeout() {
		val timer = spy(Timer(limit = 1000, start = 0))
		doCallRealMethod().`when`(timer).check()
		doReturn(1000.toLong()).`when`(timer).now()
		expect(TimeoutException::class) {
			timer.check()
		}
	}

}