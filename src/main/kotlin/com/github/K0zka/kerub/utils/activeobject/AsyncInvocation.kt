package com.github.K0zka.kerub.utils.activeobject

import java.io.Serializable

public class AsyncInvocation (
		val beanName: String,
		val methodName: String,
		val paramTypes: List<Class<out Any?>>,
		val args: List<Any>) : Serializable {
}