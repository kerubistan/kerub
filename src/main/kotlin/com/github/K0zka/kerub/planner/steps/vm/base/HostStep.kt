package com.github.K0zka.kerub.planner.steps.vm.base

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

interface HostStep : AbstractOperationalStep {
	val host: Host
}