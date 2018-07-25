package com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs

import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.services.NfsDaemonService
import com.github.kerubistan.kerub.model.services.NfsService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.utils.join

object ShareNfsFactory : AbstractOperationalStepFactory<ShareNfs>() {

	/**
	 * The hosts must be running NFS daemon already, there must not be an NFS share for the directory
	 * and there must be file allocations
	 */
	override fun produce(state: OperationalState): List<ShareNfs> =
			factoryFeature(state.controllerConfig.storageTechnologies.nfsEnabled) {
				val fileAllocations = state.vStorage.values.map {
					it.dynamic?.allocations ?: listOf()
				}.join().filterIsInstance(VirtualStorageFsAllocation::class.java)

				getHostsRunningNfs(state).map { hostColl ->
					fileAllocations
							.filter { it.hostId == hostColl.stat.id }
							.filterNot { allocation ->
								hostColl.config!!.services.any {
									it is NfsService && it.directory == allocation.mountPoint
								}
							}
							.map {
								ShareNfs(host = hostColl.stat, directory = it.mountPoint)
							}
				}.join()
			}

	private fun getHostsRunningNfs(state: OperationalState): List<HostDataCollection> =
			state.hosts.values.filter {
				it.config?.services?.any { it is NfsDaemonService } ?: false
			}
}