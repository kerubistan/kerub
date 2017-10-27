package com.github.kerubistan.kerub.utils.activeobject

import java.io.Serializable

class AsyncInvocation(
		val beanName: String,
		val methodName: String,
		val paramTypes: List<Class<out Any?>>,
		val args: List<Any>) : Serializable