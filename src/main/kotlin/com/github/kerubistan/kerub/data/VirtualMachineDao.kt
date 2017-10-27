package com.github.kerubistan.kerub.data

import com.github.kerubistan.kerub.model.VirtualMachine
import java.util.UUID

interface VirtualMachineDao :
		ListableCrudDao<VirtualMachine, UUID>,
		DaoOperations.SimpleSearch<VirtualMachine>,
		AssetDao<VirtualMachine> {
	fun listByAttachedStorage(virtualDiskId: UUID): List<VirtualMachine>
}