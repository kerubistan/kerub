package com.github.kerubistan.kerub.planner.steps.storage

import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.RecyclingStorageDevice
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFsFactory
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinumFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLvFactory

object UnallocateDiskFactory : StepFactoryCollection(listOf(
		UnAllocateLvFactory,
		UnAllocateFsFactory,
		UnAllocateGvinumFactory
)) {
	override val expectationHints = super.expectationHints + setOf(VirtualMachineAvailabilityExpectation::class, StorageAvailabilityExpectation::class)
	override val problemHints = super.problemHints + RecyclingStorageDevice::class
}