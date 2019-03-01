package com.github.kerubistan.kerub.utils.activeobject

import com.github.kerubistan.kerub.utils.getLogger
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

open class Executor : ApplicationContextAware {
	private var appCtx: ApplicationContext? = null

	companion object {
		private val logger = getLogger(Executor::class)
	}

	override fun setApplicationContext(applicationContext: ApplicationContext?) {
		appCtx = applicationContext
	}

	fun execute(invocation: AsyncInvocation) {
		logger.debug("executing async invocation on bean")
		val bean = appCtx?.getBean(invocation.beanName)!!
		val types = Array<Class<out Any?>>(invocation.paramTypes.size) { invocation.paramTypes[it] }
		val method = bean::class.java.getMethod(invocation.methodName,
				*types)
		val args = Array<Any?>(invocation.args.size) { invocation.args[it] }
		method.invoke(bean, *args)
	}
}