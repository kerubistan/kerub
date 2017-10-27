package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.VirtualNetworkDao
import com.github.kerubistan.kerub.model.VirtualNetwork
import org.infinispan.Cache
import java.util.UUID

class VirtualNetworkDaoImpl(cache: Cache<UUID, VirtualNetwork>, eventListener: EventListener, auditManager: AuditManager)
	: AbstractAssetDao<VirtualNetwork>(cache, eventListener, auditManager), VirtualNetworkDao {
	override fun getEntityClass(): Class<VirtualNetwork> = VirtualNetwork::class.java
}