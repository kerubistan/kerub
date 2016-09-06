package com.github.K0zka.kerub.model.collection

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic

data class VirtualMachineDataCollection(
		override val stat: VirtualMachine,
		override val dynamic: VirtualMachineDynamic?)
: DataCollection<VirtualMachine, VirtualMachineDynamic>