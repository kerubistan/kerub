package com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.utils.junix.ssh.openssh.OpenSsh

class RemoteBlockCopyExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val stepExecutor: StepExecutor<AbstractOperationalStep>
) : StepExecutor<RemoteBlockCopy> {
	override fun execute(step: RemoteBlockCopy) {
		stepExecutor.execute(step.allocationStep)
		hostCommandExecutor.execute(step.sourceHost) { session ->
			OpenSsh.copyBlockDevice(
					session,
					sourceDevice = step.sourceAllocation.getPath(step.sourceDevice.id),
					targetDevice = step.allocationStep.allocation.getPath(step.targetDevice.id),
					targetAddress = step.allocationStep.host.address
			)
		}
	}
}