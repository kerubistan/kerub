package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.utils.iscsiSharedDisks
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object TgtdIscsiUnshareFactory : AbstractOperationalStepFactory<TgtdIscsiUnshare>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<TgtdIscsiUnshare> =
			iscsiSharedDisks(state).map { (diskData, allocations) ->
				allocations
						.filter { allocation ->
							state.hosts[allocation.hostId]?.dynamic?.status == HostStatus.Up
									&& state.vms.values.none {
										it.dynamic?.status == VirtualMachineStatus.Up
									&& it.stat.virtualStorageLinks.none {
											link ->
											link.virtualStorageId == diskData.stat.id
										}
							}
						}
						.map { allocation ->
							TgtdIscsiUnshare(
									host = requireNotNull(state.hosts[allocation.hostId]).stat,
									allocation = allocation,
									vstorage = diskData.stat
							)
						}
			}.join()
}