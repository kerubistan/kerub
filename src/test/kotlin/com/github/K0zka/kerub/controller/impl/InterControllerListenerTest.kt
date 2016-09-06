package com.github.K0zka.kerub.controller.impl

import com.github.K0zka.kerub.controller.HostAssignedMessage
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.on
import com.github.K0zka.kerub.verify
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test
import java.util.UUID
import javax.jms.ObjectMessage

class InterControllerListenerTest {

	val hostManager: HostManager = mock()

	var hostDao: HostDao = mock()

	val message : ObjectMessage = mock()

	var impl : InterControllerListener? = null

	@Before
	fun setup() {
		impl = InterControllerListener(hostManager, hostDao)
	}

	@Test
	fun onMessageWithHostAssigned() {
		val controllerId = "test-controller"
		val hostId = UUID.randomUUID()
		val host = Host(
				id = hostId,
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "TEST"
		               )
		on(hostDao.get( eq(hostId) )).thenReturn(
				        host
		                                          )
		on(message.`object`).thenReturn(HostAssignedMessage(hostId, controllerId))
		impl!!.onMessage(message)

		verify(hostManager).connectHost(eq(host))
	}

}