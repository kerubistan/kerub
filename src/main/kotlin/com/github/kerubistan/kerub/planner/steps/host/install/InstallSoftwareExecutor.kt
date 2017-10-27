package com.github.kerubistan.kerub.planner.steps.host.install

import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.planner.StepExecutor

class InstallSoftwareExecutor(val hostManager: HostManager) : StepExecutor<InstallSoftware> {
	override fun execute(step: InstallSoftware) {
		throw NotImplementedError()
	}
}