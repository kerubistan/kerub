package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.junix.dd.Dd

class LocalBlockCopyExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val stepExecutor: StepExecutor<AbstractOperationalStep>
) : StepExecutor<LocalBlockCopy> {

	override fun execute(step: LocalBlockCopy) {
		stepExecutor.execute(step.allocationStep)
		hostCommandExecutor.execute(step.allocationStep.host) { session ->
			Dd.copy(
					session,
					step.sourceAllocation.getPath(step.sourceDevice.id),
					step.allocationStep.allocation.getPath(step.targetDevice.id))
		}
	}
}