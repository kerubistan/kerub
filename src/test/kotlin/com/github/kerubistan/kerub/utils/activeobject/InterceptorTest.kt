package com.github.kerubistan.kerub.utils.activeobject

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.aopalliance.intercept.MethodInvocation
import org.junit.Before
import org.junit.Test

class InterceptorTest {
	private val queue: InvocationQueue = mock()
	private val invocation: MethodInvocation = mock()

	var interceptor: Interceptor? = null

	@Before
	fun setup() {
		interceptor = Interceptor("TEST", queue)
	}

	@Test
	fun invoke() {
		whenever(invocation.method).thenReturn(Any::class.java.getMethod("toString"))
		whenever(invocation.arguments).thenReturn(Array<Any>(0) { "" })
		interceptor!!.invoke(invocation)
		verify(queue).send(any())
	}
}