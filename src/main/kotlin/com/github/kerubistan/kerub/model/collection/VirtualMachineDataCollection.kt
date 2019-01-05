package com.github.kerubistan.kerub.model.collection

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic

data class VirtualMachineDataCollection(
		override val stat: VirtualMachine,
		override val dynamic: VirtualMachineDynamic?)
	: DataCollection<VirtualMachine, VirtualMachineDynamic> {
	init {
		this.validate()
	}
}