package com.github.kerubistan.kerub.model.expectations

import java.util.UUID

interface VirtualMachineReference {
	val referredVmIds: List<UUID>
}