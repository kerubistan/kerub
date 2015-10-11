package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.planner.StepExecutor

public class PowerDownExecutor(private val hostManager : HostManager) : StepExecutor<PowerDownHost> {
	override fun execute(step: PowerDownHost) {
		hostManager.getPowerManager(step.host).off()
	}
}