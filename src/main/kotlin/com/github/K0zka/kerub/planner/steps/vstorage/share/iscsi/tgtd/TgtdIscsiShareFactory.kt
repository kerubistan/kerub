package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.collection.VirtualStorageDataCollection
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

object TgtdIscsiShareFactory : AbstractOperationalStepFactory<TgtdIscsiShare>() {
	override fun produce(state: OperationalState): List<TgtdIscsiShare> {
		return unsharedDisks(state).map {
			storageData ->
			TgtdIscsiShare(
					host = requireNotNull(state.hosts[storageData.dynamic?.allocation?.hostId]?.stat),
					vstorage = storageData.stat,
					devicePath = (storageData.dynamic?.allocation as VirtualStorageLvmAllocation).path
			)
		}
	}

	/**
	 * List of pairs of disks and disk dynamic of virtual storage devices that are allocated but not shared
	 */
	private fun unsharedDisks(state: OperationalState): List<VirtualStorageDataCollection> {
		return state.vStorage.values.filterNotNull().filter {
			// OR other block-storage, even files, but not virtual disks
			it.dynamic?.allocation is VirtualStorageLvmAllocation
		}.filterNot {
			// it is not shared yet
			val hostData = state.hosts[it.dynamic?.allocation?.hostId]
			val services = hostData?.config?.services
			services?.contains(IscsiService(vstorageId = it.stat.id)) ?: false
		}
	}
}