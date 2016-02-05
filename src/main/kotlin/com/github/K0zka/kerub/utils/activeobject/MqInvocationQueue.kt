package com.github.K0zka.kerub.utils.activeobject

import org.springframework.jms.core.JmsTemplate

public class MqInvocationQueue(val jmsTemplate: JmsTemplate) : InvocationQueue {
	override fun send(invocation: AsyncInvocation) {
		jmsTemplate.send { it?.createObjectMessage(invocation) }
	}
}