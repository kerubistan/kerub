package com.github.kerubistan.kerub.planner.steps.storage.lvm.base

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

abstract class AbstractAllocateStorageExecutor<T : AbstractOperationalStep, U>(
		protected val virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<T, U>() {

}