package com.github.kerubistan.kerub.planner.steps.host.recycle

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import kotlin.reflect.KClass

object RecycleHostFactory : AbstractOperationalStepFactory<RecycleHost>() {

	override val problemHints = setOf(RecyclingHost::class)
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<RecycleHost> =
			state.hosts.values.filter { (host, dyn) ->
				//the host is being recycled
				host.recycling &&
						isHostFree(host, state) &&
						canDropFreeHost(host, dyn)
			}.map {
				RecycleHost(it.stat)
			}

	private fun canDropFreeHost(
			host: Host, dyn: HostDynamic?
	) =
			//it is either dedicated and shut down, or not dedicated
			((host.dedicated && (dyn == null || dyn.status == HostStatus.Down))
					|| !host.dedicated)

	private fun isHostFree(host: Host, state: OperationalState) =
			//no more disk allocations on it
			state.vStorage.values.none {
				it.dynamic?.allocations?.any { allocation ->
					allocation.hostId == host.id
				} ?: false
			} &&
					//no vms running on it
					state.index.runningVms.none {
						it.dynamic?.hostId == host.id
					}

}