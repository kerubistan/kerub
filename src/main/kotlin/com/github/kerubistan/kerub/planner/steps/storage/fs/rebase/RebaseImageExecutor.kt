package com.github.kerubistan.kerub.planner.steps.storage.fs.rebase

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
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