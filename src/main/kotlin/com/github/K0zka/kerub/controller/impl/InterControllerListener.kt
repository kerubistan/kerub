package com.github.K0zka.kerub.controller.impl

import com.github.K0zka.kerub.controller.EntityEventMessage
import com.github.K0zka.kerub.controller.HostAssignedMessage
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.utils.getLogger
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.ObjectMessage

public class InterControllerListener(
		private val hostManager: HostManager,
		private val hostDao: HostDao
) : MessageListener {
	companion object {
		val logger = getLogger(InterControllerListener::class)
	}

	override fun onMessage(message: Message?) {
		val msg = (message as ObjectMessage).getObject()!!
		when (msg) {
			is EntityEventMessage  -> {

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