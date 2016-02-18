package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

abstract class AbstractVStorageCreateFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>() {

	companion object {
		fun listRunningHosts(state: OperationalState) = state.hosts.values.filter { state.hostDyns[it.id]?.status == HostStatus.Up }

		fun listStorageNotAllocated(state: OperationalState): List<VirtualStorageDevice> {
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
			return storageNotAllocated
		}

	}

}