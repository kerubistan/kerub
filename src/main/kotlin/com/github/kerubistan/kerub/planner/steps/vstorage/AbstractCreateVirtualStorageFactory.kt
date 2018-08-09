package com.github.kerubistan.kerub.planner.steps.vstorage

import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

abstract class AbstractCreateVirtualStorageFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>() {

	companion object {
		fun listStorageNotAllocated(
				state: OperationalState,
				types: List<VirtualDiskFormat> = VirtualDiskFormat.values().toList()): List<VirtualStorageDevice> =

				state.vStorage.values.filter { it.dynamic == null }
				.filter { storage ->
					storage.stat.expectations.any {
						it is StorageAvailabilityExpectation
								&& types.contains(it.format)
					}
							||
							state.vmsThatMustStart.any { vm ->
								vm.stat.virtualStorageLinks.any { link ->
									link.virtualStorageId == storage.stat.id
								}
							}
				}.map { it.stat }
	}

}