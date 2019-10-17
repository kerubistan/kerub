package com.github.kerubistan.kerub.planner.steps.storage

import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

abstract class AbstractCreateVirtualStorageFactory<S : AbstractOperationalStep> : AbstractOperationalStepFactory<S>()