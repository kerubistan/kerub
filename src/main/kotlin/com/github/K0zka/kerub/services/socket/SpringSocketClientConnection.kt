package com.github.K0zka.kerub.services.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.model.Entity
import com.github.K0zka.kerub.model.messages.EntityMessage
import com.github.K0zka.kerub.model.messages.Message
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.update
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import kotlin.reflect.KClass

class SpringSocketClientConnection(
		private val session: WebSocketSession,
		private val mapper: ObjectMapper) : ClientConnection {

	var subscriptions: Map<KClass<*>, List<ChannelSubscription>> = services.map { it.key to listOf<ChannelSubscription>() }.toMap()

	private companion object {
		val logger = getLogger(SpringSocketClientConnection::class)
	}

	@Synchronized
	override
	fun removeSubscription(channel: String) {
		val sub = ChannelSubscription.fromChannel(channel)
		subscriptions = subscriptions.update(
				sub.entityClass,
				{ it - sub },
				{ listOf() }
		)
	}

	@Synchronized
	override
	fun addSubscription(channel: String) {
		val sub = ChannelSubscription.fromChannel(channel)
		subscriptions = subscriptions.update(
				sub.entityClass,
				{ it + sub },
				{ listOf(sub) }
		)
	}

	private fun getEntityType(channel: String) = requireNotNull(addressToEntity[channel], { "Channel $channel not mapped!" })

	override fun filterAndSend(msg: Message) {
		if (msg is EntityMessage) {
			val entity = msg.obj
			val entityClass = entity.javaClass.kotlin as KClass<out Entity<out Any>>
			val classChannel = channels.get(entityClass)
			if (classChannel == null) {
				logger.warn("Entity type not handled: {}", entityClass)
			} else {
				synchronized(subscriptions) {
					if (subscriptions[entityClass]?.any { it.interested(msg) } ?: false)
						session.sendMessage(TextMessage(mapper.writeValueAsString(msg)))
				}
			}
		}
	}
}