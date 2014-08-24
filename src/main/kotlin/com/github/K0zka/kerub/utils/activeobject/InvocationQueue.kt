package com.github.K0zka.kerub.utils.activeobject

public trait InvocationQueue {
	fun send(invocation : AsyncInvocation) : Unit
}