package com.github.K0zka.kerub.planner.steps.vstorage

import com.github.K0zka.kerub.planner.steps.AbstractStepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vstorage.fs.create.CreateImageFactory
import com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create.CreateGvinumVolumeFactory
import com.github.K0zka.kerub.planner.steps.vstorage.lvm.create.CreateLvFactory

object CreateDiskFactory : AbstractStepFactoryCollection(
		listOf(
				CreateLvFactory,
				CreateImageFactory,
				CreateGvinumVolumeFactory
		)
)