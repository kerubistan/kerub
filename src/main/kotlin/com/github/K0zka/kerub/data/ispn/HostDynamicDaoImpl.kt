package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.HistoryDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import org.infinispan.Cache
import java.util.UUID

class HostDynamicDaoImpl(
		cache: Cache<UUID, HostDynamic>,
		historyDao: HistoryDao<HostDynamic>,
		eventListener: EventListener)
	: AbtractDynamicEntityDao<HostDynamic>(cache, historyDao, eventListener), HostDynamicDao