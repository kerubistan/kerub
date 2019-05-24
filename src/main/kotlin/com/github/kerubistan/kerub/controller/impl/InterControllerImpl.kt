package com.github.kerubistan.kerub.controller.impl

import com.github.kerubistan.kerub.controller.EntityEventMessage
import com.github.kerubistan.kerub.controller.InterController
import org.springframework.jms.core.JmsTemplate
import java.io.Serializable
import javax.jms.DeliveryMode
import javax.jms.ObjectMessage
import javax.jms.Session

class InterControllerImpl(private val jmsTemplate: JmsTemplate) : InterController {
	override fun sendToController(controllerId: String, msg: Serializable) {
		jmsTemplate.send("jms.queue.kerub-mq-$controllerId") {
			createObjectMessage(it, msg)
		}
	}

	private fun createObjectMessage(it: Session, msg: Serializable): ObjectMessage? {
		val objMsg = it.createObjectMessage()
		objMsg.jmsPriority = priorityForMsgType(msg)
		objMsg.jmsDeliveryMode = deliveryModeForMsgType(msg)
		objMsg.`object` = msg
		return objMsg
	}

	private fun deliveryModeForMsgType(msg: Serializable): Int =
			when (msg) {
				is EntityEventMessage -> DeliveryMode.NON_PERSISTENT
				else -> DeliveryMode.PERSISTENT
			}

	private fun priorityForMsgType(msg: Serializable): Int =
			when (msg) {
				is EntityEventMessage -> 6
				else -> 5
			}

	override fun broadcast(msg: Serializable) {
		jmsTemplate.send("jms.topic.kerub-broadcast") {
			createObjectMessage(it, msg)
		}
	}

}