package com.github.K0zka.kerub.planner.steps.vstorage.fs.rebase

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import java.math.BigInteger

class RebaseImageExecutor(
		private val exec: HostCommandExecutor,
		private val dynDao: VirtualStorageDeviceDynamicDao
) : AbstractStepExecutor<RebaseImage, BigInteger>() {
	override fun perform(step: RebaseImage): BigInteger {
		TODO()
	}

	override fun update(step: RebaseImage, updates: BigInteger) {
		TODO()
	}
}