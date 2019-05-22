package com.github.kerubistan.kerub.utils.activeobject

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation

class Interceptor(private val bean: String, val queue: InvocationQueue) : MethodInterceptor {
	override fun invoke(invocation: MethodInvocation?): Any? {
		queue.send(
				AsyncInvocation(
						bean,
						invocation!!.method!!.name!!,
						invocation.method!!.parameterTypes!!.toList(),
						invocation.arguments!!.toList()
				)
		)
		return null
	}
}