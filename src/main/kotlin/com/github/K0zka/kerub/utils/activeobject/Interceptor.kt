package com.github.K0zka.kerub.utils.activeobject

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

public class Interceptor (val bean : String, val queue: InvocationQueue) : MethodInterceptor {
	override fun invoke(invocation: MethodInvocation?): Any? {
		queue.send(
				AsyncInvocation(
								bean,
								invocation!!.getMethod()!!.getName()!!,
								invocation.getMethod()!!.getParameterTypes()!!.toList(),
								invocation.getArguments()!!.toList()
				               )
		          )
		return null
	}
}