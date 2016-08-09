package com.github.K0zka.kerub.planner.steps.vstorage

import com.github.K0zka.kerub.planner.steps.StepFactoryCollection
import com.github.K0zka.kerub.planner.steps.vstorage.fs.create.CreateImageFactory
import com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create.CreateGvinumVolumeFactory
import com.github.K0zka.kerub.planner.steps.vstorage.lvm.create.CreateLvFactory

object CreateDiskFactory : StepFactoryCollection(
		listOf(
				CreateLvFactory,
				CreateImageFactory,
				CreateGvinumVolumeFactory
		)
)