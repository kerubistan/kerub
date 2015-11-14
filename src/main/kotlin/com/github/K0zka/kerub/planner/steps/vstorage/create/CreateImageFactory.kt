package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object CreateImageFactory : AbstractOperationalStepFactory<CreateImage>() {
	override fun produce(state: OperationalState): List<CreateImage> {
		val vmsThatMustRun = state.vms.values.filter {
			vm ->
			vm.expectations.any {
				expectation ->
				expectation is VirtualMachineAvailabilityExpectation
						&& expectation.up
			}
		}
		val storageNotAllocated = state.vStorage.values.filterNot { state.vStorageDyns.contains(it.id) }
				.filter {
					storage ->
					vmsThatMustRun.any {
						vm ->
						vm.virtualStorageLinks.any {
							link ->
							link.virtualStorageId == storage.id
						}
					}
				}
		val runningHosts = state.hosts.filter { state.hostDyns[it.key]?.status == HostStatus.Up }

		var steps = listOf<CreateImage>()

		runningHosts.values.forEach {
			host ->
			steps += storageNotAllocated.map {
				storage ->
				CreateImage(
						device = storage,
						host = host
				)
			}
		}

		return steps
	}
}