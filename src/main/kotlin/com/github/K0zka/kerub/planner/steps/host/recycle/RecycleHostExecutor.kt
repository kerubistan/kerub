package com.github.K0zka.kerub.planner.steps.host.recycle

import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.planner.StepExecutor

class RecycleHostExecutor(
		private val hostDao: HostDao,
		private val hostDynamicDao: HostDynamicDao
) : StepExecutor<RecycleHost> {
	override fun execute(step: RecycleHost) =
			step.host.id.let {
				hostDao.remove(it)
				hostDynamicDao.remove(it)
			}
}