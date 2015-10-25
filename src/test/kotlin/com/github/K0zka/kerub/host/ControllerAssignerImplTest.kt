package com.github.K0zka.kerub.host

import com.github.K0zka.kerub.anyString
import com.github.K0zka.kerub.controller.HostAssignedMessage
import com.github.K0zka.kerub.controller.InterController
import com.github.K0zka.kerub.data.AssignmentDao
import com.github.K0zka.kerub.data.ControllerDao
import com.github.K0zka.kerub.data.dynamic.ControllerDynamicDao
import com.github.K0zka.kerub.matchAny
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.controller.Assignment
import com.github.K0zka.kerub.model.dynamic.ControllerDynamic
import com.github.K0zka.kerub.times
import com.github.K0zka.kerub.verify
import com.github.k0zka.finder4j.backtrack.BacktrackService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
public class ControllerAssignerImplTest {
	var backtrack : BacktrackService? = null

	@Mock
	var controllerDao : ControllerDao? = null

	@Mock
	var controllerDynamicDao : ControllerDynamicDao? = null

	@Mock
	var hostAssignmentDao : AssignmentDao? = null

	@Mock
	var interController : InterController? = null

	@Before
	fun setup() {
		backtrack = BacktrackService()
		Mockito.`when`(controllerDynamicDao?.listAll()).thenReturn(listOf(
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
		val result = ControllerAssignerImpl(
				backtrack!!,
				controllerDao!!,
				controllerDynamicDao!!,
				hostAssignmentDao!!,
				interController!!)
				.assignControllers(listOf(host1, host2))

		val assignment = Assignment(
				id = UUID.randomUUID(),
				controller = "",
				hostId = UUID.randomUUID()
		                           )
		verify(hostAssignmentDao!!, times(2)).add( matchAny(Assignment::class.java,
		                                                    assignment
		                                                    ) )

		verify(interController!!, times(2)).sendToController( anyString(), matchAny(HostAssignedMessage::class.java,
		                                                                            HostAssignedMessage(
				                                                                            hostId = UUID.randomUUID(),
		                                                                                    conrollerId = ""
		                                                                                               )
		                                                                            ) )

	}
}