package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.HistoryDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import org.infinispan.Cache
import java.util.UUID

class HostDynamicDaoImpl(
		cache: Cache<UUID, HostDynamic>,
		historyDao: HistoryDao<HostDynamic>,
		eventListener: EventListener)
	: AbstractDynamicEntityDao<HostDynamic>(cache, historyDao, eventListener), HostDynamicDao