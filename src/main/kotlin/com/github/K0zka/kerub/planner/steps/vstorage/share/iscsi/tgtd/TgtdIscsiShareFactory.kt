package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object TgtdIscsiShareFactory : AbstractOperationalStepFactory<TgtdIscsiShare>() {
	override fun produce(state: OperationalState): List<TgtdIscsiShare> {
		return unsharedDisks(state).map {
			diskAndDyn ->
			TgtdIscsiShare(
					host = requireNotNull(state.hosts[diskAndDyn.second.allocation.hostId]),
					vstorage = diskAndDyn.first,
					devicePath = (diskAndDyn.second.allocation as VirtualStorageLvmAllocation).path
			)
		}
	}

	/**
	 * List of pairs of disks and disk dynamic of virtual storage devices that are allocated but not shared
	 */
	private fun unsharedDisks(state: OperationalState): List<Pair<VirtualStorageDevice, VirtualStorageDeviceDynamic>> {
		return state.vStorage.values.map {
			vStorage ->
			val dyn = state.vStorageDyns[vStorage.id]
			dyn?.let { vStorage to requireNotNull(dyn) }
		}.filterNotNull().filter {
			// OR other block-storage, even files, but not virtual disks
			it.second.allocation is VirtualStorageLvmAllocation
		}.filterNot {
			// it is not shared yet
			val hostDynamic = state.hostDyns[it.second.allocation.hostId]
			val services = hostDynamic?.services
			services?.contains(IscsiService(vstorageId = it.first.id)) ?: false
		}
	}
}