package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.PoolDao
import com.github.kerubistan.kerub.model.Pool
import org.infinispan.Cache
import java.util.UUID

class PoolDaoImpl(cache: Cache<UUID, Pool>, eventListener: EventListener, auditManager: AuditManager) : PoolDao,
		AbstractAssetDao<Pool>(cache, eventListener, auditManager) {
	override fun getEntityClass(): Class<Pool> = Pool::class.java
}