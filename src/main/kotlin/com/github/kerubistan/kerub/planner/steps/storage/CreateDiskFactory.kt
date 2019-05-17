package com.github.kerubistan.kerub.planner.steps.storage

import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageFactory
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLvFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create.CreateLvmPoolFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend.ExtendLvmPoolFactory

object CreateDiskFactory : StepFactoryCollection(
		listOf(
				CreateLvFactory,
				CreateLvmPoolFactory,
				ExtendLvmPoolFactory,
				CreateThinLvFactory,
				CreateImageFactory,
				TruncateImageFactory,
				CreateGvinumVolumeFactory
		)
) {
	override val expectationHints = super.expectationHints + setOf(VirtualMachineAvailabilityExpectation::class, StorageAvailabilityExpectation::class)
}