package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import org.infinispan.Cache
import org.infinispan.query.dsl.Expression
import java.util.UUID

class VirtualMachineDaoImpl(cache: Cache<UUID, VirtualMachine>, eventListener: EventListener, auditManager: AuditManager)
: VirtualMachineDao, AbstractAssetDao<VirtualMachine>(cache, eventListener, auditManager) {

	override fun getEntityClass(): Class<VirtualMachine> =
			VirtualMachine::class.java

	override fun listByAttachedStorage(virtualDiskId: UUID) =
			cache.queryBuilder(VirtualMachine::class)
					.having(Expression.property(VirtualMachine::virtualStorageIdStr.name))
					.containsAny(virtualDiskId.toString())
					.list<VirtualMachine>()

}