package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import org.infinispan.Cache
import org.infinispan.query.Search
import org.infinispan.query.dsl.Query
import java.util.UUID

class VirtualMachineDynamicDaoImpl(cache: Cache<UUID, VirtualMachineDynamic>,
								   historyDao : HistoryDao<VirtualMachineDynamic>,
								   eventListener: EventListener)
: AbtractDynamicEntityDao<VirtualMachineDynamic>(cache, historyDao, eventListener), VirtualMachineDynamicDao {

	override fun findByHostId(hostId: UUID): List<VirtualMachineDynamic> =
			Search.getQueryFactory(cache)
					.from(VirtualMachineDynamic::class.java)
					.having("hostIdStr").eq(hostId.toString())
					.toBuilder<Query>()
					.build()
					.list<VirtualMachineDynamic>()
}