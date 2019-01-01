package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.utils.join

abstract class AbstractFailingStorageDeviceDetector<T : Problem> : ProblemDetector<T> {
	final override fun detect(plan: Plan) = plan.state.hosts.values.mapNotNull { host: HostDataCollection ->
		host.dynamic?.storageDeviceHealth?.mapNotNull { (device, healthy) ->
			if (healthy) null else
				createProblems(host, device, plan)
		}
	}.join().join()

	/**
	 * Create problem instances out of failed device with the host record.
	 */
	abstract fun createProblems(host: HostDataCollection, device: String, plan: Plan): List<T>?
}