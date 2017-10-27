package com.github.kerubistan.kerub.planner.steps.vm.migrate

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineFactory

object MigrateVirtualMachineFactory : StepFactoryCollection(listOf(KvmMigrateVirtualMachineFactory))