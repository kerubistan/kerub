package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.planner.steps.AbstractStepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineFactory

object StartVirtualMachineFactory : AbstractStepFactoryCollection(listOf(
		KvmStartVirtualMachineFactory
		//virtualbox, xen, bhyve, etc
))
