package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import java.util.UUID
import org.infinispan.Cache
import com.github.K0zka.kerub.data.EventListener

public class VirtualMachineDaoImpl(cache: Cache<UUID, VirtualMachine>, eventListener : EventListener)
: VirtualMachineDao, ListableIspnDaoBase<VirtualMachine, UUID>(cache, eventListener) {
	override fun getEntityClass(): Class<VirtualMachine> {
		return javaClass<VirtualMachine>()
	}
}