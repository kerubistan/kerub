package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.utils

import com.github.K0zka.kerub.model.collection.VirtualStorageDataCollection
import com.github.K0zka.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState

/**
 * List of pairs of disks and disk dynamic of virtual storage devices that are allocated but not shared
 */
fun unsharedDisks(state: OperationalState): List<VirtualStorageDataCollection> {
	return state.vStorage.values.filterNotNull().filter {
		// TODO: even files, except virtual disk formats other than RAW
		val allocation = it.dynamic?.allocation
		allocation is VirtualStorageBlockDeviceAllocation
	}.filterNot {
		// it is not shared yet
		vStorage ->
		val hostData = state.hosts[vStorage.dynamic?.allocation?.hostId]
		val services = hostData?.config?.services
		services?.any { it is IscsiService && it.vstorageId == vStorage.stat.id } ?: false
	}
}
