package com.github.K0zka.kerub.planner.steps.host.install

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.StepExecutor

class InstallSoftwareExecutor(val hostManager : HostManager) : StepExecutor<InstallSoftware> {
	override fun execute(step: InstallSoftware) {
		throw NotImplementedError()
	}
}