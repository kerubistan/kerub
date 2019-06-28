package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import io.github.kerubistan.kroki.collections.join
import io.github.kerubistan.kroki.time.now
import java.util.Date

object VmOnOldHostDetector : ProblemDetector<VmOnOldHost> {
	override fun detect(plan: Plan): Collection<VmOnOldHost> =
			plan.state.index.runningHosts.filter {
				it.stat.endOfPlannedLifetime?.before(Date()) ?: false
			}.map { (stat, _, _) ->
				plan.state.vmsOnHost(stat.id).filter { /* TODO: check if this is ok by SLA */ true }
						.map { vm: VirtualMachine ->
							VmOnOldHost(
									host = stat,
									vm = vm,
									hostExpiredSince = (now() - (stat.endOfPlannedLifetime!!.time)).toInt()
							)
						}
			}.join()
}