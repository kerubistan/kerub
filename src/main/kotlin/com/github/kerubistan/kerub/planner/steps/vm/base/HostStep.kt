package com.github.kerubistan.kerub.planner.steps.vm.base

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep

interface HostStep : AbstractOperationalStep {
	val host: Host
}