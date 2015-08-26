package com.github.K0zka.kerub.model.dynamic

import com.github.K0zka.kerub.model.Entity
import java.util.UUID

public data class VirtualMachineDynamic(
		override
		val id: UUID
                                       ) : Entity<UUID>
