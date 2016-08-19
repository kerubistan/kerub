package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualNetworkDao
import com.github.K0zka.kerub.model.VirtualNetwork
import org.infinispan.Cache
import java.util.UUID

class VirtualNetworkDaoImpl(cache: Cache<UUID, VirtualNetwork>, eventListener: EventListener) : ListableIspnDaoBase<VirtualNetwork, UUID>(cache, eventListener), VirtualNetworkDao {
	override fun getEntityClass(): Class<VirtualNetwork> = VirtualNetwork::class.java
}