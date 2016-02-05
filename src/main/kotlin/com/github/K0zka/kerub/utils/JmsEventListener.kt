package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.messages.Message
import org.springframework.jms.core.JmsTemplate

class JmsEventListener(val template: JmsTemplate) : EventListener {
	override fun send(message: Message) {
		template.send({ it?.createObjectMessage(message) })
	}

}