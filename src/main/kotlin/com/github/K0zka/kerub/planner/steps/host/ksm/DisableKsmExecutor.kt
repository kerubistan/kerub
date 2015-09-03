package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.StepExecutor

public class DisableKsmExecutor(val exec : HostCommandExecutor) : StepExecutor<DisableKsm>{
	override fun execute(step: DisableKsm) {
		controlKsm(step.host, exec, true)
	}
}