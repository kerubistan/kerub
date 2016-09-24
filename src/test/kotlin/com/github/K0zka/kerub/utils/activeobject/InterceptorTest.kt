package com.github.K0zka.kerub.utils.activeobject

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.aopalliance.intercept.MethodInvocation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

class InterceptorTest {
	val queue: InvocationQueue = mock()
	val invocation: MethodInvocation = mock()

	var interceptor: Interceptor? = null

	@Before
	fun setup() {
		interceptor = Interceptor("TEST", queue)
	}

	@Test
	fun invoke() {
		Mockito.`when`(invocation.method)!!.thenReturn(Any::class.java.getMethod("toString"))
		Mockito.`when`(invocation.arguments)!!.thenReturn(Array<Any>(0, { "" }))
		interceptor!!.invoke(invocation)
		Mockito.verify(queue)!!.send(any<AsyncInvocation>())
	}
}