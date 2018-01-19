package com.github.kerubistan.kerub.host

import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.github.k0zka.finder4j.backtrack.BacktrackServiceImpl
import com.github.kerubistan.kerub.controller.HostAssignedMessage
import com.github.kerubistan.kerub.controller.InterController
import com.github.kerubistan.kerub.data.AssignmentDao
import com.github.kerubistan.kerub.data.ControllerDao
import com.github.kerubistan.kerub.data.dynamic.ControllerDynamicDao
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.controller.Assignment
import com.github.kerubistan.kerub.model.controller.AssignmentType
import com.github.kerubistan.kerub.model.dynamic.ControllerDynamic
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID
import java.util.concurrent.ForkJoinPool

class ControllerAssignerImplTest {
	var backtrack: BacktrackService? = null

	val controllerDao: ControllerDao = mock()

	val controllerDynamicDao: ControllerDynamicDao = mock()

	var hostAssignmentDao: AssignmentDao = mock()

	var interController: InterController = mock()

	@Before
	fun setup() {
		backtrack = BacktrackServiceImpl(ForkJoinPool(4))
		Mockito.`when`(controllerDynamicDao.listAll()).thenReturn(listOf(
				ControllerDynamic(
						controllerId = "ctrl-1",
						addresses = listOf(),
						maxHosts = 64,
						totalHosts = 0
				),
				ControllerDynamic(
						controllerId = "ctrl-2",
						addresses = listOf(),
						maxHosts = 64,
						totalHosts = 10
				)

		))
	}

	@After
	fun cleanup() {
		backtrack?.stop()
	}

	@Test
	fun assignControllers() {
		val host1 = Host(address = "127.0.0.1", dedicated = true, publicKey = "")
		val host2 = Host(address = "127.0.0.2", dedicated = true, publicKey = "")
		ControllerAssignerImpl(
				backtrack!!,
				controllerDao,
				controllerDynamicDao,
				hostAssignmentDao,
				interController)
				.assignControllers(listOf(host1, host2))

		val assignment = Assignment(
				id = UUID.randomUUID(),
				controller = "",
				entityId = UUID.randomUUID(),
				type = AssignmentType.host
		)
		verify(hostAssignmentDao, times(2)).add(any<Assignment>())

		verify(interController, times(2)).sendToController(any(), any<HostAssignedMessage>())

	}
}