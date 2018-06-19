package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.utils

import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState

/**
 * Map of disks to list of allocations
 *  - the server with the allocation must be up
 *  - the allocation must be either a block device or a raw format file
 *  - there must not be an iscsi service running for this allocation
 */
fun iscsiShareableDisks(state: OperationalState): Map<VirtualStorageDataCollection, List<VirtualStorageAllocation>> =
		mapBy(state, ::isNotShared)

fun iscsiSharedDisks(state: OperationalState): Map<VirtualStorageDataCollection, List<VirtualStorageAllocation>> =
		mapBy(state, ::isShared)

private inline fun mapBy(state: OperationalState, filter: (HostDataCollection, VirtualStorageDataCollection) -> Boolean)
		: Map<VirtualStorageDataCollection, List<VirtualStorageAllocation>> =
		state.vStorage.values.mapNotNull { coll ->
			coll to (coll.dynamic?.allocations?.filter { allocation ->
				isRawData(allocation) && state.hosts[allocation.hostId]?.let {
					it.dynamic?.status == HostStatus.Up
							&& filter(it, coll)
				} ?: false
			} ?: listOf())
		}.toMap().filterValues { it.isNotEmpty() }

private fun isShared(hostData: HostDataCollection, coll: VirtualStorageDataCollection): Boolean =
		!isNotShared(hostData, coll)

private fun isNotShared(hostData: HostDataCollection, coll: VirtualStorageDataCollection): Boolean =
		hostData.config?.services?.let {
			it.none {
				it is IscsiService && it.vstorageId == coll.stat.id
			}
		} ?: false

private fun isRawData(allocation: VirtualStorageAllocation) =
		(allocation is VirtualStorageBlockDeviceAllocation
				|| (allocation is VirtualStorageFsAllocation && allocation.type == VirtualDiskFormat.raw))
