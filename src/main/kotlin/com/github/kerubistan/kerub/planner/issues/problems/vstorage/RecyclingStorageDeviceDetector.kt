package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector

object RecyclingStorageDeviceDetector : ProblemDetector<RecyclingStorageDevice> {
	override fun detect(plan: Plan): Collection<RecyclingStorageDevice> =
			plan.state.vStorage.values.mapNotNull {
				if (it.stat.recycling) {
					RecyclingStorageDevice(it.stat)
				} else null
			}
}