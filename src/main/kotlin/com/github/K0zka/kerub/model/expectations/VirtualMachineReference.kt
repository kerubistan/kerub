package com.github.K0zka.kerub.model.expectations

import java.util.UUID

interface VirtualMachineReference {
	val referredVmIds : List<UUID>
}