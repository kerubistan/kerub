package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.duplicate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.issues.problems.vms.VmOnRecyclingHost
import com.github.kerubistan.kerub.planner.issues.problems.vstorage.VStorageDeviceOnRecyclingHost
import com.github.kerubistan.kerub.planner.steps.vstorage.block.duplicate.AbstractBlockDuplicateFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.util.hasEnoughFreeCapacity
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

object DuplicateToLvmFactory : AbstractBlockDuplicateFactory<DuplicateToLvm>() {

	override val problemHints = setOf(RecyclingHost::class, VmOnRecyclingHost::class, VStorageDeviceOnRecyclingHost::class)
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<DuplicateToLvm> =
			state.vStorage.filterValues {
				it.stat.readOnly && it.dynamic?.allocations?.isNotEmpty() ?: false
			}.map { vstorage ->

				vstorage.value.dynamic!!.allocations.filter { state.hosts[it.hostId]?.dynamic?.status == HostStatus.Up }
						.filterIsInstance<VirtualStorageBlockDeviceAllocation>()
						.map { sourceAllocation ->
							val sourceHost = requireNotNull(state.hosts[sourceAllocation.hostId])
							val allocatedOnHosts = vstorage.value.dynamic!!.allocations.map { it.hostId }
							state.runningHosts
									.filterNot { allocatedOnHosts.contains(it.stat.id) }.map { targetHostColl ->
										targetHostColl.stat.capabilities?.storageCapabilities
												?.filterIsInstance(LvmStorageCapability::class.java)
												?.filter {
													hasEnoughFreeCapacity(it, vstorage.value.stat, targetHostColl.dynamic)
															&& isHostkeyInstalled(sourceHost, targetHostColl)
												}?.map {
													DuplicateToLvm(
															vStorageDevice = vstorage.value.stat,
															targetHost = targetHostColl.stat,
															target = VirtualStorageLvmAllocation(
																	hostId = targetHostColl.stat.id,
																	vgName = it.volumeGroupName,
																	actualSize = sourceAllocation.actualSize,
																	path = "/dev/${it.volumeGroupName}/${vstorage.value.stat.id}"
															),
															sourceHost = sourceHost.stat,
															source = sourceAllocation
													)
												}
									}
						}
			}.join().join().filterNotNull().join()

	private fun isHostkeyInstalled(sourceHost: HostDataCollection, targetHostColl: HostDataCollection) =
			(sourceHost.config?.publicKey != null &&
					targetHostColl.config?.acceptedPublicKeys?.contains(sourceHost.config.publicKey) ?: false)

}