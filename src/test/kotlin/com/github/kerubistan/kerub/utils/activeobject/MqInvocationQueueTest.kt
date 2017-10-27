package com.github.kerubistan.kerub.utils.activeobject

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import javax.jms.Session

class MqInvocationQueueTest {
	val template: JmsTemplate = mock()
	val session: Session = mock()
	@Test
	fun send() {
		val queue = MqInvocationQueue(template)
		doAnswer({ (it.arguments!![0] as MessageCreator).createMessage(session) })
				.`when`(template)!!.send(any<MessageCreator>())
		queue.send(AsyncInvocation("", "", listOf(), listOf()))
		verify(template).send(any<MessageCreator>())
		verify(session).createObjectMessage(any<AsyncInvocation>())
	}
}