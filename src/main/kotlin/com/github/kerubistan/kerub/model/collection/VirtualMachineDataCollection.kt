package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import java.util.UUID

data class VirtualMachineDataCollection(
		override val stat: VirtualMachine,
		override val dynamic: VirtualMachineDynamic?)
	: DataCollection<UUID, VirtualMachine, VirtualMachineDynamic> {
	init {
		this.validate()
	}
}