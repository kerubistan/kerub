package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.model.dynamic.DynamicEntity
import org.infinispan.Cache
import java.util.UUID

abstract class AbstractDynamicEntityDao<T : DynamicEntity>(
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

}