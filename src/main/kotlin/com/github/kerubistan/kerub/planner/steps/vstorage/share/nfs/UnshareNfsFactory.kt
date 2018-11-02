package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.UnusedService
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object UnshareNfsFactory : AbstractOperationalStepFactory<UnshareNfs>() {
	override val problemHints = setOf(UnusedService::class)
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<UnshareNfs> =
			state.hosts.values.map { hostColl ->
				hostColl.stat to hostColl.config?.services?.filterIsInstance<NfsService>()?.filterNot { nfsService ->
					isDirectoryUsedByAnyMounts(nfsService.directory, hostColl.stat, state)
				}
			}.map { hostAndServices ->
				hostAndServices.second?.map {
					UnshareNfs(host = hostAndServices.first, directory = it.directory)
				}
			}.filterNotNull().join()

	private fun isDirectoryUsedByAnyMounts(directory: String, host: Host,
										   state: OperationalState): Boolean =
			state.hosts.values.any {
				it.config?.services?.any {
					it is NfsMount && it.remoteDirectory == directory && it.remoteHostId == host.id
				} ?: false
			}
}