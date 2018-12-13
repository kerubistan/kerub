package com.github.kerubistan.kerub.planner.issues.problems.vms

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.now
import java.util.Date

object VmOnOldHostDetector : ProblemDetector<VmOnOldHost> {
	override fun detect(plan: Plan): Collection<VmOnOldHost> =
		plan.state.runningHosts.filter { it.stat.endOfPlannedLifetime?.before(Date()) ?: false }.map { (stat, dynamic, config) ->
			plan.state.vmsOnHost(stat.id).filter { /* TODO: check if this is ok by SLA */ true }.map { vm: VirtualMachine ->
				VmOnOldHost(
						host = stat,
						vm = vm,
						hostExpiredSince = (now() - (stat.endOfPlannedLifetime!!.time)).toInt()
				)
			}
		}.join()
}