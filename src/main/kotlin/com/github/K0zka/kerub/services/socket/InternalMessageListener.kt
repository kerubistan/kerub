package com.github.K0zka.kerub.services.socket

import javax.jms.MessageListener
import javax.jms.Message
import com.github.K0zka.kerub.services.socket.messages.EntityUpdateMessage
import javax.jms.ObjectMessage
import java.util.ArrayList
import java.util.LinkedList
import com.github.K0zka.kerub.model.Entity

public class InternalMessageListener : MessageListener {

	val channels : MutableList<WebSocketNotifier> = LinkedList()

	fun addSocketListener(listener : WebSocketNotifier) {
		channels.add(listener)
	}

	fun removeSocketListener(listener : WebSocketNotifier) {
		channels.remove(listener)
	}

	override fun onMessage(message: Message?) {
		val obj = (message as ObjectMessage).getObject()!!
		for(notifier in channels) {
			notifier.onUpdate(obj as Entity<Any>, message.getJMSTimestamp())
		}
	}
}