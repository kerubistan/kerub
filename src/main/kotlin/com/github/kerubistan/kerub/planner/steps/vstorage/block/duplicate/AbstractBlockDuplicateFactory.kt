package com.github.kerubistan.kerub.planner.steps.vstorage.block.duplicate

import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

abstract class AbstractBlockDuplicateFactory<B : AbstractBlockDuplicate<*>> : AbstractOperationalStepFactory<B>() {
}