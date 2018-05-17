package com.github.kerubistan.kerub.planner.steps.vstorage

import com.github.kerubistan.kerub.planner.steps.StepFactoryCollection
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.unallocate.UnAllocateFsFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.gvinum.unallocate.UnAllocateGvinumFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.unallocate.UnAllocateLvFactory

object UnallocateDiskFactory : StepFactoryCollection(listOf(
		UnAllocateLvFactory,
		UnAllocateFsFactory,
		UnAllocateGvinumFactory
))