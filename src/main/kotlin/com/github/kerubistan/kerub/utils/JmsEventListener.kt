package com.github.kerubistan.kerub.utils

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.messages.Message
import org.springframework.jms.core.JmsTemplate

class JmsEventListener(val template: JmsTemplate) : EventListener {
	override fun send(message: Message) {
		template.send({ it?.createObjectMessage(message) })
	}

}