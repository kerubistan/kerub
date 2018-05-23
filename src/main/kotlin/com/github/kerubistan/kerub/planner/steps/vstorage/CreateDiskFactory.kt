package com.github.kerubistan.kerub.planner.steps.vstorage

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.create.CreateImageFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.create.CreateGvinumVolumeFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create.CreateLvFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.create.CreateThinLvFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.create.CreateLvmPoolFactory

object CreateDiskFactory : StepFactoryCollection(
		listOf(
				CreateLvFactory,
				CreateLvmPoolFactory,
				CreateThinLvFactory,
				CreateImageFactory,
				CreateGvinumVolumeFactory
		)
)