package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import org.infinispan.Cache
import java.util.UUID

open abstract class AbtractDynamicEntityDao<T : DynamicEntity>(
		cache: Cache<UUID, T>,
		private val historyDao: HistoryDao<T>,
		eventListener: EventListener)
	: IspnDaoBase<T, UUID>(cache, eventListener) {

	override fun update(id: UUID, retrieve: (UUID) -> T, change: (T) -> T) {
		val oldData = retrieve(id)
		val newData = change(oldData)

		historyDao.log(oldData, newData)
		update(
				newData
		)
	}

	override fun add(entity: T): UUID {
		return super.add(entity)
	}

	override fun remove(entity: T) {
		super.remove(entity)
	}

	override fun remove(id: UUID) {
		super.remove(id)
	}
}