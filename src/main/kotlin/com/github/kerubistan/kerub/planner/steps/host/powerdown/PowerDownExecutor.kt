package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.planner.StepExecutor

class PowerDownExecutor(private val hostManager: HostManager) : StepExecutor<PowerDownHost> {
	override fun execute(step: PowerDownHost) {
		hostManager.powerDown(step.host)
	}
}