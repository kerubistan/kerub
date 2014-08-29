package com.github.K0zka.kerub.utils.activeobject

import org.junit.Test
import org.mockito.Mock
import org.springframework.jms.core.JmsTemplate
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.Mockito
import org.mockito.Matchers
import org.springframework.jms.core.MessageCreator

RunWith(javaClass<MockitoJUnitRunner>())
public class MqInvocationQueueTest {
	Mock
	var template : JmsTemplate? = null
	Test
	fun send() {
		val queue = MqInvocationQueue(template!!)
		queue.send(AsyncInvocation("", "", listOf(), listOf()))
		Mockito.verify(template)!!.send(Matchers.any(javaClass<MessageCreator>()));
	}
}