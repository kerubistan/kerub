package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector

object CpuOverheatDetector : ProblemDetector<CpuOverheat> {
	override fun detect(plan: Plan): Collection<CpuOverheat> =
			plan.state.hosts.values.mapNotNull { hostData: HostDataCollection ->
				hostData.dynamic?.cpuTemperature?.mapIndexedNotNull { coreId, temperature ->
					if (temperature > plan.state.controllerConfig.cpuNormalTemperature.max)
						coreId to temperature
					else null
				}?.toMap()
						?.let { overheatingCores ->
							if (overheatingCores.isNotEmpty())
								CpuOverheat(host = hostData.stat, cores = overheatingCores)
							else null
						}
			}
}