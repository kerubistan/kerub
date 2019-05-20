package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.HistoryDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import io.github.kerubistan.kroki.strings.toUUID
import org.infinispan.Cache
import org.infinispan.query.dsl.Query
import java.util.UUID

class HostDynamicDaoImpl(
		cache: Cache<UUID, HostDynamic>,
		historyDao: HistoryDao<HostDynamic>,
		eventListener: EventListener)
	: AbstractDynamicEntityDao<HostDynamic>(cache, historyDao, eventListener), HostDynamicDao {
	override fun listRunningHosts(uuids: List<UUID>, max: Int?): List<UUID> =
			cache.advancedCache.queryBuilder(HostDynamic::class)
					.select(HostDynamic::idStr.name)
					.having(HostDynamic::status.name).eq(HostStatus.Up)
					.and().having(HostDynamic::idStr.name).`in`(uuids.map { it.toString() }).toBuilder<Query>()
					.list<Array<Any>>().map {
						it[0].toString().toUUID()
					}
}