package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.issues.problems.common.recyclingHostMap
import com.github.kerubistan.kerub.utils.join

object VStorageDeviceOnRecyclingHostDetector : ProblemDetector<VStorageDeviceOnRecyclingHost> {
	override fun detect(plan: Plan): Collection<VStorageDeviceOnRecyclingHost> {
		val recyclingHosts = recyclingHostMap(plan)
		return if (recyclingHosts.isEmpty()) {
			//performance consideration: host recycling should be relatively rare, so if there are none (usually)
			//then we should not check all the virtual storage allocations (lot more than hosts)
			listOf()
		} else {
			plan.state.vStorage.values.map { collection ->
				collection.dynamic?.allocations?.let { allocations ->
					allocations.filter { it.hostId in recyclingHosts.keys }.map { allocation ->
						VStorageDeviceOnRecyclingHost(
								host = recyclingHosts.getValue(allocation.hostId).stat,
								vstorage = collection.stat,
								allocation = allocation
						)
					}
				} ?: listOf()
			}.join()
		}
	}

}