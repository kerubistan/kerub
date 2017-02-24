package com.github.K0zka.kerub.controller.impl

import com.github.K0zka.kerub.controller.EntityEventMessage
import com.github.K0zka.kerub.controller.HostAssignedMessage
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.messages.SessionEventMessage
import com.github.K0zka.kerub.services.socket.InternalMessageListener
import com.github.K0zka.kerub.utils.getLogger
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.ObjectMessage

class InterControllerListener(
		private val hostManager: HostManager,
		private val hostDao: HostDao,
		private val listener: InternalMessageListener
) : MessageListener {
	companion object {
		val logger = getLogger(InterControllerListener::class)
	}

	override fun onMessage(message: Message?) {
		val msg = (message as ObjectMessage).`object`!!
		when (msg) {
			is SessionEventMessage -> {
				logger.info("close sockets for ${msg.sessionId}")
				listener.removeSocketListener(msg.sessionId)
			}
			is EntityEventMessage -> {
				TODO("")
			}
			is HostAssignedMessage -> {
				logger.info("msg: host assigned {}", msg.hostId)
				hostManager.connectHost(
						hostDao.get(msg.hostId)!!
				)
			}
		}
	}
}