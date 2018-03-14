package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.join

object UnshareNfsFactory : AbstractOperationalStepFactory<UnshareNfs>() {
	override fun produce(state: OperationalState): List<UnshareNfs> =
			state.hosts.values.map { hostColl ->
				hostColl.stat to hostColl.config?.services?.filterIsInstance<NfsService>()?.filterNot { nfsService ->
					isDirectoryUsedByAnyAllocations(nfsService.directory, hostColl.stat, state)
				}
			}.map { hostAndServices ->
				hostAndServices.second?.map {
					UnshareNfs(host = hostAndServices.first, directory = it.directory)
				}
			}.filterNotNull().join()

	private fun isDirectoryUsedByAnyAllocations(directory: String, host: Host,
												state: OperationalState): Boolean =
			state.vms.values.filter { it.dynamic?.status == VirtualMachineStatus.Up }
					.map { it.stat.virtualStorageLinks.map { it.virtualStorageId } }
					.join()
					.mapNotNull { state.vStorage[it]?.dynamic?.allocations?.filterIsInstance<VirtualStorageFsAllocation>() }
					.join().any { it.hostId == host.id && it.mountPoint == directory }
}