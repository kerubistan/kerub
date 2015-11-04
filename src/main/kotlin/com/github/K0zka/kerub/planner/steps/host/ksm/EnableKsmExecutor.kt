package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.StepExecutor

public class EnableKsmExecutor(val exec : HostCommandExecutor) : StepExecutor<EnableKsm> {
	override fun execute(step: EnableKsm) {
		controlKsm(step.host, exec, true)
	}
}