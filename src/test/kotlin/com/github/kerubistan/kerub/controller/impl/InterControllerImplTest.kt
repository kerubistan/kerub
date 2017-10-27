package com.github.kerubistan.kerub.controller.impl

import com.github.kerubistan.kerub.controller.EntityEventMessage
import com.github.kerubistan.kerub.model.Project
import com.github.kerubistan.kerub.model.messages.EntityAddMessage
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test
import org.springframework.jms.core.JmsTemplate
import java.util.UUID

class InterControllerImplTest {
	var jmsTemplate: JmsTemplate = mock()
	var interControllerImpl: InterControllerImpl? = null

	@Before
	fun setup() {
		interControllerImpl = InterControllerImpl(jmsTemplate)
	}

	@Test
	fun sendToControllerEntityEvent() {
		val controllerId = "test-controller"
		val message = EntityAddMessage(
				obj = Project(
						id = UUID.randomUUID(),
						created = System.currentTimeMillis(),
						description = "",
						expectations = listOf(),
						name = "test"
				             ),
				date = System.currentTimeMillis()
		                                       )
		interControllerImpl!!.sendToController(controllerId, EntityEventMessage(
				message
		                                                                       ))

	}

	@Test
	fun broadcast() {

		interControllerImpl!!.broadcast(EntityAddMessage(
				obj = Project(
						id = UUID.randomUUID(),
						created = System.currentTimeMillis(),
						description = "",
						expectations = listOf(),
						name = "test"
				             ),
				date = System.currentTimeMillis()
		                                                ))

	}
}