package com.github.K0zka.kerub.services.socket

import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.ObjectMessage
import com.github.K0zka.kerub.model.messages.Message as KerubMessage

public class InternalMessageListener : MessageListener {

	val channels : MutableMap<String, ClientConnection> = hashMapOf()

	fun addSocketListener(id: String, conn : ClientConnection) {
		channels.put(id, conn)
	}

	fun removeSocketListener(id : String) {
		channels.remove(id)
	}

	override fun onMessage(message: Message?) {
		val obj = (message as ObjectMessage).getObject()!!
		for(connection in channels) {
			connection.value.filterAndSend(obj as KerubMessage)
		}
	}
}