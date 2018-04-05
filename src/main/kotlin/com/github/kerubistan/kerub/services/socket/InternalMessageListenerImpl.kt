package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.model.messages.EntityMessage
import com.github.kerubistan.kerub.planner.Planner
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.updateMutable
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.ObjectMessage
import com.github.kerubistan.kerub.model.messages.Message as KerubMessage

open class InternalMessageListenerImpl(private val planner: Planner) : MessageListener, InternalMessageListener {

	companion object {
		private val logger = getLogger(InternalMessageListenerImpl::class)
	}

	private val channels: MutableMap<String, Map<String, ClientConnection>> = hashMapOf()

	override fun addSocketListener(sessionId: String, socketId: String, conn: ClientConnection) {
		synchronized(this) {
			channels.updateMutable(sessionId,
					init = { mapOf(socketId to conn) },
					mapper = { it + (socketId to conn) }
			)
		}
	}

	override fun subscribe(sessionId: String, socketId: String, channel: String) {
		synchronized(this) {
			channels[sessionId]?.get(socketId)?.addSubscription(channel)
		}
	}

	override fun unsubscribe(sessionId: String, socketId: String, channel: String) {
		synchronized(this) {
			channels[sessionId]?.get(socketId)?.removeSubscription(channel)
		}
	}

	override fun removeSocketListener(id: String) {
		synchronized(this) {
			channels.remove(id)?.forEach { connection ->
				connection.value.close()
			}
		}
	}

	override fun onMessage(message: Message?) {
		val obj = (message as ObjectMessage).`object`!!

		if (obj is EntityMessage) {
			planner.onEvent(obj)
		}

		for (session in synchronized(this) { channels }) {
			for (connection in session.value) {
				try {
					connection.value.filterAndSend(obj as KerubMessage)
				} catch (e: IllegalStateException) {
					logger.info("Could not deliver msg", e)
				}
			}
		}
	}
}