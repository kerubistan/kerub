package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.CrudDao
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.model.Entity
import com.github.kerubistan.kerub.model.messages.EntityAddMessage
import com.github.kerubistan.kerub.model.messages.EntityRemoveMessage
import com.github.kerubistan.kerub.model.messages.EntityUpdateMessage
import com.github.kerubistan.kerub.utils.byId
import com.github.kerubistan.kerub.utils.now
import org.infinispan.Cache
import org.infinispan.metadata.Metadata
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent
import org.infinispan.notifications.cachelistener.event.CacheEntryEvent
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent
import org.infinispan.notifications.cachelistener.filter.CacheEventConverter
import org.infinispan.notifications.cachelistener.filter.CacheEventFilter
import org.infinispan.notifications.cachelistener.filter.EventType

abstract class IspnDaoBase<T : Entity<I>, I>(protected val cache: Cache<I, T>,
											 protected val eventListener: EventListener) : CrudDao<T, I> {
	override fun add(entity: T): I {
		cache.put(entity.id, entity)
		eventListener.send(EntityAddMessage(entity, now()))
		return entity.id
	}

	override fun addAll(entities: Collection<T>) =
		cache.putAll(entities.byId())

	override fun get(id: I): T? = cache[id]

	override fun get(ids: Collection<I>): List<T> =
			cache.advancedCache.getAll(ids.toHashSet()).values.toList()

	override fun remove(entity: T) {
		eventListener.send(EntityRemoveMessage(entity))
		cache.remove(entity.id)
	}

	override fun remove(id: I) {
		val entity = get(id)
		if (entity != null)
			eventListener.send(EntityRemoveMessage(entity))
		cache.remove(id)
	}

	override fun update(entity: T) {
		eventListener.send(EntityUpdateMessage(entity, now()))
		cache.put(entity.id!!, entity)
	}

	@Listener(clustered = true, sync = false)
	abstract class AbstractListener<I, T>(private val cache: Cache<I, T>, private val action: (T) -> Boolean) {
		fun onEvent(event: CacheEntryEvent<I, T>) {
			if (action(event.value)) {
				cache.removeListener(this)
			}
		}
	}

	class CreateListener<I, T>(cache: Cache<I, T>, action: (T) -> Boolean) : AbstractListener<I, T>(cache, action) {
		@CacheEntryCreated
		fun onCreate(event: CacheEntryCreatedEvent<I, T>) = super.onEvent(event)
	}

	class RemoveListener<I, T>(cache: Cache<I, T>, action: (T) -> Boolean) : AbstractListener<I, T>(cache, action) {
		@CacheEntryRemoved
		fun onRemove(event: CacheEntryRemovedEvent<I, T>) = super.onEvent(event)
	}

	class ChangeListener<I, T>(cache: Cache<I, T>, action: (T) -> Boolean) : AbstractListener<I, T>(cache, action) {
		@CacheEntryModified
		fun onChange(event: CacheEntryModifiedEvent<I, T>) = super.onEvent(event)
	}

	class IdFilter<I>(private val id: I) : CacheEventFilter<I, Any> {
		override fun accept(
				key: I,
				oldValue: Any?,
				oldMeta: Metadata?,
				newValue: Any?,
				newMeta: Metadata?,
				type: EventType?)
				= key == id
	}

	class Converter<I, T> : CacheEventConverter<I, T, T> {
		override fun convert(key: I, oldValue: T, oldMeta: Metadata?, newValue: T, newMeta: Metadata?, type: EventType?): T =
				newValue
	}

	private fun listen(listener: Any) = cache.advancedCache.addListener(listener)

	private fun listen(listener: Any, id: I) = cache.advancedCache.addListener(listener, IdFilter(id), Converter<I, T>())

	override fun listenCreate(action: (T) -> Boolean) = listen(CreateListener(cache, action))

	override fun listenCreate(id: I, action: (T) -> Boolean) = listen(CreateListener(cache, action), id)

	override fun listenUpdate(action: (T) -> Boolean) = listen(ChangeListener(cache, action))

	override fun listenUpdate(id: I, action: (T) -> Boolean) = listen(ChangeListener(cache, action), id)

	override fun listenDelete(action: (T) -> Boolean) = listen(RemoveListener(cache, action))

	override fun listenDelete(id: I, action: (T) -> Boolean) = listen(RemoveListener(cache, action), id)

	override fun waitFor(id: I, action: (T) -> Boolean) {
		val value = get(id)
		if (value == null) {
			listenCreate(id, {
				action(it)
				true
			})
		} else {
			action(value)
		}
	}
}