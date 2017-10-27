package com.github.kerubistan.kerub.data.dynamic

import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import java.util.UUID

interface VirtualMachineDynamicDao : DynamicDao<VirtualMachineDynamic> {
	fun findByHostId(hostId: UUID): List<VirtualMachineDynamic>
}