package com.github.kerubistan.kerub.hypervisor.kvm

import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection

/**
 * Collection of all the technical data needed for a VM disk attachment.
 */
data class VirtualStorageLinkInfo(
		val link: VirtualStorageLink,
		val device: VirtualStorageDataCollection,
		val storageHost: HostDataCollection
)