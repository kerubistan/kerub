package com.github.kerubistan.kerub.controller.impl

import com.github.kerubistan.kerub.controller.HostAssignedMessage
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.services.socket.InternalMessageListener
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import java.util.UUID
import javax.jms.ObjectMessage

class InterControllerListenerTest {

	val hostManager: HostManager = mock()

	private var hostDao: HostDao = mock()

	val message : ObjectMessage = mock()

	private val listener : InternalMessageListener = mock()

	var impl : InterControllerListener? = null

	@Before
	fun setup() {
		impl = InterControllerListener(hostManager, hostDao, listener)
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
		whenever(hostDao[eq(hostId)]).thenReturn(
				        host
		                                          )
		whenever(message.`object`).thenReturn(HostAssignedMessage(hostId, controllerId))
		impl!!.onMessage(message)

		verify(hostManager).connectHost(eq(host))
	}

}