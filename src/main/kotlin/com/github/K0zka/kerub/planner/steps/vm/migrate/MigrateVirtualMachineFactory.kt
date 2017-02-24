package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.planner.steps.StepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineFactory

object MigrateVirtualMachineFactory : StepFactoryCollection(listOf(KvmMigrateVirtualMachineFactory))