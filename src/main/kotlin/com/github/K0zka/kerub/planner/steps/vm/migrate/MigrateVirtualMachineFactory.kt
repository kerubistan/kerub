package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.planner.steps.StepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineFactory

object MigrateVirtualMachineFactory : StepFactoryCollection(listOf(KvmStartVirtualMachineFactory))