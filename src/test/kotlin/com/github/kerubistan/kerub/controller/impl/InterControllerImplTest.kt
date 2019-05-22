package com.github.kerubistan.kerub.controller.impl

import com.github.kerubistan.kerub.controller.EntityEventMessage
import com.github.kerubistan.kerub.model.Project
import com.github.kerubistan.kerub.model.messages.EntityAddMessage
import com.nhaarman.mockito_kotlin.mock
import io.github.kerubistan.kroki.time.now
import org.junit.Before
import org.junit.Test
import org.springframework.jms.core.JmsTemplate
import java.util.UUID

class InterControllerImplTest {
	private var jmsTemplate: JmsTemplate = mock()
	private var interControllerImpl: InterControllerImpl? = null

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
						created = now(),
						description = "",
						expectations = listOf(),
						name = "test"
				             ),
				date = now()
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
						created = now(),
						description = "",
						expectations = listOf(),
						name = "test"
				             ),
				date = now()
		                                                ))

	}
}