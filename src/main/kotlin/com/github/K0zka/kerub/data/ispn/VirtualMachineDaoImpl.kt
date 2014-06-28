package com.github.K0zka.kerub.data.ispn

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import java.util.UUID
import org.infinispan.Cache

public class VirtualMachineDaoImpl(cache: Cache<UUID, VirtualMachine>)
: VirtualMachineDao, IspnDaoBase<VirtualMachine, UUID>(cache) {
	override fun listAll(): List<VirtualMachine> {
		return cache.values().toList()
	}
}