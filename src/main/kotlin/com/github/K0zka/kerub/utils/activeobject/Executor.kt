package com.github.K0zka.kerub.utils.activeobject

import com.github.K0zka.kerub.utils.getLogger
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

public open class Executor : ApplicationContextAware {
	private var appCtx : ApplicationContext? = null

	private companion object val logger = getLogger(Executor::class)

	override fun setApplicationContext(applicationContext: ApplicationContext?) {
		appCtx = applicationContext
	}
	public fun execute(invocation : AsyncInvocation) {
		logger.debug("executing async invocation on bean")
		val bean = appCtx?.getBean(invocation.beanName)!!
		val types = Array<Class<out Any?>>(invocation.paramTypes.size, { invocation.paramTypes[it] })
		val method = bean.javaClass.getMethod(invocation.methodName,
		                         parameterTypes = *types)
		val args = Array<Any?>(invocation.args.size, { invocation.args[it] })
		method.invoke(bean, *args)
	}
}