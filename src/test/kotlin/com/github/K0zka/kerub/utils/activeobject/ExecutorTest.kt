package com.github.K0zka.kerub.utils.activeobject

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.context.ApplicationContext

class ExecutorTest {
	var appCtx: ApplicationContext = mock()
	var service : HelloService = mock()

	var executor: Executor? = null

	@Before
	fun setup() {
		executor = Executor()
		executor!!.setApplicationContext(appCtx)
		whenever(appCtx.getBean("helloService")).thenReturn(service)
	}

	interface HelloService {
		fun hello(name : String)
	}

	@Test
	fun execute() {
		executor!!.execute(
				AsyncInvocation(
						beanName = "helloService",
						args = listOf("world"),
						methodName = "hello",
						paramTypes = listOf(String::class.java)))
		Mockito.verify(service)!!.hello("world")
	}
}