package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.Constrained
import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.CacheSizeExpectation
import com.github.kerubistan.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.kerubistan.kerub.model.expectations.ClockFrequencyExpectation
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.model.expectations.CpuArchitectureExpectation
import com.github.kerubistan.kerub.model.expectations.MemoryClockFrequencyExpectation
import com.github.kerubistan.kerub.model.expectations.NoMigrationExpectation
import com.github.kerubistan.kerub.model.expectations.NotSameHostExpectation
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualStorageExpectation
import com.github.kerubistan.kerub.planner.issues.violations.plan.NoMigrationExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.CacheSizeExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.ChassisManufacturerExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.ClockFrequencyExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.CoreDedicationExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.CpuArchitectureExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.MemoryClockFrequencyExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.NotSameHostExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vm.VirtualMachineAvailabilityExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vstorage.NotSameStorageExpectationViolationDetector
import com.github.kerubistan.kerub.planner.issues.violations.vstorage.StorageAvailabilityExpectationViolationDetector
import com.github.kerubistan.kerub.utils.getLogger

class PlanViolationDetectorImpl : PlanViolationDetector {

	companion object {
		private val logger = getLogger(PlanViolationDetectorImpl::class)

		internal fun checkVmExpectation(vm: VirtualMachine, expectation: VirtualMachineExpectation,
										plan: Plan) =
				when (expectation) {
					is CacheSizeExpectation -> CacheSizeExpectationViolationDetector.check(vm, expectation, plan.state)
					is ChassisManufacturerExpectation ->
						ChassisManufacturerExpectationViolationDetector.check(vm, expectation, plan.state)
					is ClockFrequencyExpectation ->
						ClockFrequencyExpectationViolationDetector.check(vm, expectation, plan.state)
					is CoreDedicationExpectation ->
						CoreDedicationExpectationViolationDetector.check(vm, expectation, plan.state)
					is CpuArchitectureExpectation ->
						CpuArchitectureExpectationViolationDetector.check(vm, expectation, plan.state)
					is MemoryClockFrequencyExpectation ->
						MemoryClockFrequencyExpectationViolationDetector.check(vm, expectation, plan.state)
					is NotSameHostExpectation -> NotSameHostExpectationViolationDetector
							.check(vm, expectation, plan.state)
					is VirtualMachineAvailabilityExpectation ->
						VirtualMachineAvailabilityExpectationViolationDetector.check(vm, expectation, plan.state)
					is NoMigrationExpectation ->
						NoMigrationExpectationViolationDetector.check(vm, expectation, plan)
					else -> {
						TODO("unhandled expectation: $expectation")
					}
				}

		internal fun checkVStorageExpectation(vdisk: VirtualStorageDevice,
											  expectation: VirtualStorageExpectation,
											  plan: Plan)
				= when (expectation) {
			is NotSameStorageExpectation -> NotSameStorageExpectationViolationDetector
					.check(vdisk, expectation, plan.state)
			is StorageAvailabilityExpectation ->
				StorageAvailabilityExpectationViolationDetector.check(vdisk, expectation, plan.state)
			else -> {
				logger.warn("unhandled expectation: $expectation")
				false
			}
		}

		internal fun <C : Expectation, T : Constrained<C>> listEntityViolations(
				entities: List<T>,
				plan: Plan,
				check: (T, C, Plan) -> Boolean): Map<Constrained<C>, List<Expectation>> =
				entities.map { entity ->
					entity to entity.expectations.filterNot { check(entity, it, plan) }
				}.filter { it.second.isNotEmpty() }.toMap()

	}

	override fun listViolations(plan: Plan): Map<Constrained<out Expectation>, List<Expectation>>
			= listVmViolations(plan) + listVStorageViolations(plan)

	private fun listVmViolations(
			plan: Plan): Map<Constrained<VirtualMachineExpectation>, List<Expectation>> =
			listEntityViolations(plan.state.vms.values.map { it.stat }, plan, ::checkVmExpectation)

	private fun listVStorageViolations(
			plan: Plan): Map<Constrained<VirtualStorageExpectation>, List<Expectation>> =
			listEntityViolations(plan.state.vStorage.values.map { it.stat }, plan, ::checkVStorageExpectation)

}