package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.planner.steps.StepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.start.virtualbox.VirtualBoxStartVirtualMachineFactory

object StartVirtualMachineFactory : StepFactoryCollection(listOf(
		KvmStartVirtualMachineFactory,
		VirtualBoxStartVirtualMachineFactory
		//xen, bhyve, etc
))
