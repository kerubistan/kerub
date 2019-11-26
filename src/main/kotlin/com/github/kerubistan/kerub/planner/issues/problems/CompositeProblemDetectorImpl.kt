package com.github.kerubistan.kerub.planner.issues.problems

import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHostDetector
import com.github.kerubistan.kerub.planner.issues.problems.hosts.UnusedServiceDetector
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.CpuOverheatDetector
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.FailingStorageDeviceDetector
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.VirtualStorageAllocationOnFailingStorageDeviceDetector
import com.github.kerubistan.kerub.planner.issues.problems.vms.VmOnRecyclingHostDetector
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.RecyclingStorageDeviceDetector
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.VStorageDeviceOnRecyclingHostDetector
import io.github.kerubistan.kroki.collections.concat

object CompositeProblemDetectorImpl : ProblemDetector<Problem> {

	private val detectors = listOf(
			//host
			RecyclingHostDetector,
			UnusedServiceDetector,
			//host hardware
			CpuOverheatDetector,
			FailingStorageDeviceDetector,
			VirtualStorageAllocationOnFailingStorageDeviceDetector,
			//vms
			VmOnRecyclingHostDetector,
			//vstorage
			VStorageDeviceOnRecyclingHostDetector,
			RecyclingStorageDeviceDetector
	)

	override fun detect(plan: Plan): Collection<Problem> = detectors.map { it.detect(plan) }.concat()
}