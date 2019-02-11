package com.github.kerubistan.kerub.planner.issues.problems.hosts

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsMount
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.utils.any
import com.github.kerubistan.kerub.utils.join

object UnusedServiceDetector : ProblemDetector<UnusedService> {
	override fun detect(plan: Plan): Collection<UnusedService> =
			plan.state.hosts.values.map { hostColl ->
				hostColl.config?.services?.filterNot { isUsed(it, plan.state, hostColl) }
						?.map { UnusedService(host = hostColl.stat, service = it) }
						?: listOf()
			}.join()

	private fun isUsed(hostService: HostService,
					   state: OperationalState,
					   hostColl: HostDataCollection): Boolean =
			when (hostService) {
				is IscsiService -> {
					//any vm's that is running and is using this virtual storage
					state.vms.values.any {
						it.dynamic?.status == VirtualMachineStatus.Up
								&& it.stat.virtualStorageLinks.any { it.virtualStorageId == hostService.vstorageId }
					}
				}
				is NfsService -> {
					state.hosts.values.any { otherHost ->
						otherHost.config?.services?.any {
							it is NfsMount
									&& it.remoteHostId == hostColl.stat.id
									&& it.remoteDirectory == hostService.directory
						} ?: false
					}
				}
				is NfsMount -> {
					//there is such vm
					state.vms.values.any {
						//that runs the same host
						it.dynamic?.status == VirtualMachineStatus.Up
								&& it.dynamic.hostId == hostColl.stat.id
								//and has a virtual disk attached
								&& it.stat.virtualStorageLinks.any { link ->
							//which disk has an FS allocation on that host
							state.vStorage[link.virtualStorageId]?.let {
								it.dynamic?.allocations?.any { alloc ->
									alloc is VirtualStorageFsAllocation
											&& alloc.hostId == hostService.remoteHostId
											&& alloc.mountPoint == hostService.remoteDirectory
								} ?: false
							} ?: false
						}
					}
				}
				is NfsDaemonService -> {
					hostColl.config?.services?.any<NfsService>() ?: false
				}
				else -> {
					TODO("unhandled: $hostService")
				}
			}
}