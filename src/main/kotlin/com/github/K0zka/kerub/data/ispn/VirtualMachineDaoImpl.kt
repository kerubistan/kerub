package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.EventListener
import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import org.infinispan.Cache
import java.util.UUID

public class VirtualMachineDaoImpl(cache: Cache<UUID, VirtualMachine>, eventListener: EventListener)
: VirtualMachineDao, ListableIspnDaoBase<VirtualMachine, UUID>(cache, eventListener) {
	override fun getEntityClass(): Class<VirtualMachine> =
			VirtualMachine::class

}