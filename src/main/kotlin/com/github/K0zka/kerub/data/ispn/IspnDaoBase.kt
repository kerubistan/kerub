package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.Entity
import org.infinispan.Cache
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.model.messages.EntityAddMessage
import com.github.K0zka.kerub.model.messages.EntityRemoveMessage
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage

open class IspnDaoBase<T : Entity<I>, I> (protected val cache : Cache<I, T>,
										  protected val eventListener : EventListener) : CrudDao<T, I> {
	override fun add(entity: T): I {
		cache.put(entity.id!!, entity)
		eventListener.send(EntityAddMessage(entity, System.currentTimeMillis()))
		return entity.id!!
	}
	override fun get(id: I): T? {
		return cache[id]
	}
	override fun remove(entity: T) {
		eventListener.send(EntityRemoveMessage(entity, System.currentTimeMillis()))
		cache.remove(entity.id)
	}
	override fun remove(id: I) {
		cache.remove(id)
	}
	override fun update(entity: T) {
		eventListener.send(EntityUpdateMessage(entity, System.currentTimeMillis()))
		cache.put(entity.id!!, entity)
	}
}