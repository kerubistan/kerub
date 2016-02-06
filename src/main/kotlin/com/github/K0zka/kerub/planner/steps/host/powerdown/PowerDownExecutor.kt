package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor

class PowerDownExecutor(private val hostManager: HostManager) : StepExecutor<PowerDownHost> {
	override fun execute(step: PowerDownHost) {
		hostManager.getPowerManager(step.host).off()
	}
}