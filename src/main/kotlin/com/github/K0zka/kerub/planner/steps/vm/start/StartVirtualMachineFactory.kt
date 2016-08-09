package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.planner.steps.StepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineFactory

object StartVirtualMachineFactory : StepFactoryCollection(listOf(
		KvmStartVirtualMachineFactory
		//virtualbox, xen, bhyve, etc
))
