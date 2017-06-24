package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.model.VirtualNetwork
import org.infinispan.Cache
import java.util.UUID

class VirtualNetworkDaoImpl(cache: Cache<UUID, VirtualNetwork>, eventListener: EventListener, auditManager: AuditManager)
	: AbstractAssetDao<VirtualNetwork>(cache, eventListener, auditManager), VirtualNetworkDao {
	override fun getEntityClass(): Class<VirtualNetwork> = VirtualNetwork::class.java
}