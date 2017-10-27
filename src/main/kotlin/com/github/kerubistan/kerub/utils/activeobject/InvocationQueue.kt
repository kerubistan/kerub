package com.github.kerubistan.kerub.utils.activeobject

interface InvocationQueue {
	fun send(invocation: AsyncInvocation): Unit
}