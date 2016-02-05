package com.github.K0zka.kerub.controller.impl

import com.github.K0zka.kerub.controller.EntityEventMessage
import com.github.K0zka.kerub.controller.InterController
import org.springframework.jms.core.JmsTemplate
import java.io.Serializable
import javax.jms.DeliveryMode
import javax.jms.ObjectMessage
import javax.jms.Session

public class InterControllerImpl(val jmsTemplate: JmsTemplate) : InterController {
	override fun sendToController(controllerId: String, msg: Serializable) {
		jmsTemplate.send ("jms.queue.kerub-mq-${controllerId}", {
			createObjectMessage(it, msg)
		})
	}

	internal fun createObjectMessage(it: Session, msg: Serializable): ObjectMessage? {
		val objMsg = it.createObjectMessage()
		objMsg.setJMSPriority(priorityForMsgType(msg))
		objMsg.setJMSDeliveryMode(deliveryModeForMsgType(msg))
		objMsg.setObject(msg)
		return objMsg
	}

	internal fun deliveryModeForMsgType(msg: Serializable): Int {
		when (msg) {
			is EntityEventMessage -> return DeliveryMode.NON_PERSISTENT
			else -> return DeliveryMode.PERSISTENT
		}
	}

	internal fun priorityForMsgType(msg: Serializable): Int {
		when (msg) {
			is EntityEventMessage -> return 6
			else -> return 5
		}
	}

	override fun broadcast(msg: Serializable) {
		jmsTemplate.send("kerub-broadcast", {
			createObjectMessage(it, msg)
		})
	}

}