package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.HistoryDao
import com.github.kerubistan.kerub.model.dynamic.DynamicEntity
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