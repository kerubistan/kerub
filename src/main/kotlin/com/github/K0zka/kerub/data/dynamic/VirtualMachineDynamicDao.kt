package com.github.K0zka.kerub.data.dynamic

import com.github.K0zka.kerub.data.CrudDao
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import java.util.UUID

interface VirtualMachineDynamicDao : CrudDao<VirtualMachineDynamic, UUID> {
	fun findByHostId(hostId: UUID) : List<VirtualMachineDynamic>
}