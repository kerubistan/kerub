package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.model.services.HostService
import com.github.kerubistan.kerub.model.services.StorageService
import java.io.Serializable

/**
 * Collection of all the technical data needed for a VM disk attachment.
 */
data class VirtualStorageLinkInfo(
		val link: VirtualStorageLink,
		val device: VirtualStorageDataCollection,
		val allocation : VirtualStorageAllocation,
		val hostServiceUsed: HostService?,
		val storageHost: HostDataCollection
) : Serializable {
	init {
		check(allocation.hostId == storageHost.stat.id) {
			"host id of allocation (${allocation.hostId}) does not match storage host id ${storageHost.stat.id}"
		}
		check(link.virtualStorageId == device.stat.id) {
			"virtual storage id of the link (${link.virtualStorageId}) does not match the device id (${device.stat.id})"
		}
		if(hostServiceUsed is StorageService) {
			check(hostServiceUsed.vstorageId == device.stat.id) {
				"virtual storage id (${hostServiceUsed.vstorageId}) of storage service " +
						"does not match device id ${device.stat.id}"
			}
		}
	}
}