package com.github.K0zka.kerub.utils.activeobject

import org.aopalliance.intercept.MethodInvocation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class) class InterceptorTest {
	@Mock
	var queue: InvocationQueue? = null
	@Mock
	var invocation: MethodInvocation? = null

	var interceptor: Interceptor? = null

	@Before
	fun setup() {
		interceptor = Interceptor("TEST", queue!!)
	}
	@Test
	fun invoke() {
		Mockito.`when`(invocation!!.method)!!.thenReturn(Any::class.java.getMethod("toString"))
		Mockito.`when`(invocation!!.arguments)!!.thenReturn(Array<Any>(0, { "" }))
		interceptor!!.invoke(invocation)
		//this is a workaround on a disagreement between kotlin and mockito
		Mockito.verify(queue)!!.send(Matchers.any(AsyncInvocation::class.java) ?: AsyncInvocation("", "", listOf(), listOf()))
	}
}