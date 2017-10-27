package com.github.kerubistan.kerub.controller.impl

import com.github.kerubistan.kerub.controller.EntityEventMessage
import com.github.kerubistan.kerub.controller.HostAssignedMessage
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.messages.SessionEventMessage
import com.github.kerubistan.kerub.services.socket.InternalMessageListener
import com.github.kerubistan.kerub.utils.getLogger
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