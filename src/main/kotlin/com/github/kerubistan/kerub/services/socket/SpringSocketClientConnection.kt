package com.github.kerubistan.kerub.services.socket

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.messages.EntityMessage
import com.github.kerubistan.kerub.model.messages.Message
import com.github.kerubistan.kerub.security.EntityAccessController
import com.github.kerubistan.kerub.utils.getLogger
import io.github.kerubistan.kroki.collections.upsert
import org.apache.shiro.subject.Subject
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import kotlin.reflect.KClass

class SpringSocketClientConnection(
		private val session: WebSocketSession,
		private val mapper: ObjectMapper,
		private val entityAccessController: EntityAccessController,
		private val subject: Subject) : ClientConnection {

	override fun close() {
		session.close(CloseStatus.NORMAL)
	}

	private var subscriptions: Map<KClass<*>, List<ChannelSubscription>> =
			services.map { it.key to listOf<ChannelSubscription>() }.toMap()

	private companion object {
		private val logger = getLogger()
	}

	@Synchronized
	override
	fun removeSubscription(channel: String) {
		val sub = ChannelSubscription.fromChannel(channel)
		subscriptions = subscriptions.upsert(
				sub.entityClass,
				{ it - sub },
				{ listOf() }
		)
	}

	@Synchronized
	override
	fun addSubscription(channel: String) {
		val sub = ChannelSubscription.fromChannel(channel)
		subscriptions = subscriptions.upsert(
				sub.entityClass,
				{ it + sub },
				{ listOf(sub) }
		)
	}

	override fun filterAndSend(msg: Message) {
		if (msg is EntityMessage) {
			val entity = msg.obj
			subject.associateWith {
				entityAccessController.checkAndDo(entity) {
					val entityClass = entity.javaClass.kotlin as KClass<out Entity<out Any>>
					val classChannel = channels[entityClass]
					if (classChannel == null) {
						logger.warn("Entity type not handled: {}", entityClass)
					} else {
						synchronized(subscriptions) {
							if (subscriptions[entityClass]?.any { it.interested(msg) } == true)
								session.sendMessage(TextMessage(mapper.writeValueAsString(msg)))
						}
					}
				}
			}.run()
		}
	}
}