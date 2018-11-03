package com.github.kerubistan.kerub.planner.steps.vm.migrate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.issues.problems.vms.VmOnRecyclingHost
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineFactory
import kotlin.reflect.KClass

object MigrateVirtualMachineFactory : StepFactoryCollection(listOf(KvmMigrateVirtualMachineFactory)) {
	override val expectationHints: Set<KClass<out Expectation>> = setOf(NotSameStorageExpectation::class, StorageAvailabilityExpectation::class)
	override val problemHints: Set<KClass<out Problem>>
			= super.problemHints + setOf(RecyclingHost::class, VmOnRecyclingHost::class)
}