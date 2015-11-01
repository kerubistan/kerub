package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor

class WakeHostExecutor(private val hostManager : HostManager) : StepExecutor<WakeHost> {
	override fun execute(step: WakeHost) {
		hostManager.getPowerManager(step.host).on()
	}
}