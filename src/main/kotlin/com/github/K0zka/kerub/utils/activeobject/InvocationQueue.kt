package com.github.K0zka.kerub.utils.activeobject

interface InvocationQueue {
	fun send(invocation: AsyncInvocation): Unit
}