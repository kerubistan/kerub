package com.github.kerubistan.kerub.data.ispn

import com.github.kerubistan.kerub.audit.AuditManager
import com.github.kerubistan.kerub.data.EventListener
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.model.VirtualMachine
import org.infinispan.Cache
import org.infinispan.query.dsl.Expression
import java.util.UUID

class VirtualMachineDaoImpl(
		cache: Cache<UUID, VirtualMachine>, eventListener: EventListener, auditManager: AuditManager
) : VirtualMachineDao, AbstractAssetDao<VirtualMachine>(cache, eventListener, auditManager) {

	override fun getEntityClass(): Class<VirtualMachine> =
			VirtualMachine::class.java

	override fun listByAttachedStorage(virtualDiskId: UUID) =
			cache.queryBuilder(VirtualMachine::class)
					.having(Expression.property(VirtualMachine::virtualStorageIdStr.name))
					.containsAny(virtualDiskId.toString())
					.list<VirtualMachine>()

	override fun listByAttachedNetwork(virtualNetworkId: UUID): List<VirtualMachine> =
			cache.queryBuilder(VirtualMachine::class)
					.having(Expression.property(VirtualMachine::virtualNetworkIdStr.name))
					.containsAll(virtualNetworkId.toString())
					.list()

}