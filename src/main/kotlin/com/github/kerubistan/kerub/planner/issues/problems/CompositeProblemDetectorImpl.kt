package com.github.kerubistan.kerub.planner.issues.problems

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHostDetector
import com.github.kerubistan.kerub.planner.issues.problems.hosts.UnusedServiceDetector
import com.github.kerubistan.kerub.planner.issues.problems.vms.VmOnRecyclingHostDetector
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.VStorageDeviceOnRecyclingHostDetector
import com.github.kerubistan.kerub.utils.join

object CompositeProblemDetectorImpl : ProblemDetector<Problem> {

	private val detectors = listOf(
			//host
			RecyclingHostDetector,
			UnusedServiceDetector,
			//vms
			VmOnRecyclingHostDetector,
			//vstorage
			VStorageDeviceOnRecyclingHostDetector
	)

	override fun detect(plan: Plan): Collection<Problem>
			= detectors.map { it.detect(plan) }.join()
}