package com.github.K0zka.kerub.services.socket

import com.github.K0zka.kerub.model.messages.EntityMessage
import com.github.K0zka.kerub.planner.Planner
import com.github.K0zka.kerub.utils.getLogger
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.ObjectMessage
import com.github.K0zka.kerub.model.messages.Message as KerubMessage

open public class InternalMessageListener (private val planner : Planner) : MessageListener {

	companion object {
		val logger = getLogger(InternalMessageListener::class)
	}

	val channels : MutableMap<String, ClientConnection> = hashMapOf()

	fun addSocketListener(id: String, conn : ClientConnection) {
		channels.put(id, conn)
	}

	fun removeSocketListener(id : String) {
		channels.remove(id)
	}

	override fun onMessage(message: Message?) {
		val obj = (message as ObjectMessage).getObject()!!

		if(obj is EntityMessage) {
			planner.onEvent(obj)
		}

		for(connection in channels) {
			try {
				connection.value.filterAndSend(obj as KerubMessage)
			} catch (e : IllegalStateException) {
				logger.info("Could not deliver msg", e)
			}
		}
	}
}