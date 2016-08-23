package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.audit.AuditManager
import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import org.infinispan.Cache
import java.util.UUID

class VirtualMachineDaoImpl(cache: Cache<UUID, VirtualMachine>, eventListener: EventListener, auditManager: AuditManager)
: VirtualMachineDao, ListableIspnDaoBase<VirtualMachine, UUID>(cache, eventListener, auditManager) {
	override fun getEntityClass(): Class<VirtualMachine> =
			VirtualMachine::class.java

}