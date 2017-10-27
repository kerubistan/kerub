package com.github.kerubistan.kerub.utils.activeobject

import org.springframework.jms.core.JmsTemplate

class MqInvocationQueue(val jmsTemplate: JmsTemplate) : InvocationQueue {
	override fun send(invocation: AsyncInvocation) {
		jmsTemplate.send { it?.createObjectMessage(invocation) }
	}
}