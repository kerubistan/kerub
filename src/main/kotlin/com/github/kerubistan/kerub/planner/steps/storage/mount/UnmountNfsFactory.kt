package com.github.kerubistan.kerub.planner.steps.storage.mount

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.normalizePath
import io.github.kerubistan.kroki.collections.join
import kotlin.reflect.KClass

object UnmountNfsFactory : AbstractOperationalStepFactory<UnmountNfs>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<UnmountNfs> {
		return state.hosts.values.mapNotNull { hostColl ->
			val storage = state.vmsOnHost(hostColl.stat.id).map { it.virtualStorageLinks }.join()
					.mapNotNull { state.vStorage[it.virtualStorageId] }
			hostColl.config?.services?.filterIsInstance<NfsMount>()?.filter {
				storage.none {
					it.dynamic?.allocations?.any {
						it is VirtualStorageFsAllocation && it.hostId != hostColl.stat.id
					} ?: false
				}
			}?.map {
				UnmountNfs(host = hostColl.stat, mountDir = it.localDirectory.normalizePath())
			}
		}.join()

	}
}