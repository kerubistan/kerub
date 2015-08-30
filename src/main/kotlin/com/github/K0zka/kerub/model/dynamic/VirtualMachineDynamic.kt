package com.github.K0zka.kerub.model.dynamic

import java.util.UUID

public data class VirtualMachineDynamic(
		override
		val id: UUID,
		override val lastUpdated: Long
                                       ) : DynamicEntity {
}
