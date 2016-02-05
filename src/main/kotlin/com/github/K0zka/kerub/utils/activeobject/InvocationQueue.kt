package com.github.K0zka.kerub.utils.activeobject

public interface InvocationQueue {
	fun send(invocation: AsyncInvocation): Unit
}