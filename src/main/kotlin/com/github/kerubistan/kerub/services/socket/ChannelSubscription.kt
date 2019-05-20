package com.github.kerubistan.kerub.services.socket

import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.messages.EntityMessage
import io.github.kerubistan.kroki.strings.toUUID
import kotlin.reflect.KClass

data class ChannelSubscription(val entityClass: KClass<out Entity<out Any>>, val id: Any?) {
	companion object {
		fun fromChannel(channel: String): ChannelSubscription {
			val channelFormatted = addSlashPrefix(addSlashPostFix(channel))
			val split = channelFormatted.split(slash).filter { it.isNotBlank() }
			val entityServiceAddress = addSlashPrefix(addSlashPostFix(split[0]))
			val entityId = split.elementAtOrNull(1)?.toUUID()
			return ChannelSubscription(requireNotNull(addressToEntity[entityServiceAddress]), entityId)
		}
	}

	fun interested(msg: EntityMessage): Boolean {
		return id == null || msg.obj.id == id
	}
}