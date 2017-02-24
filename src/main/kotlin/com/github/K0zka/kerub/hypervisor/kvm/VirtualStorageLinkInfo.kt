package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.collection.HostDataCollection
import com.github.K0zka.kerub.model.collection.VirtualStorageDataCollection

/**
 * Collection of all the technical data needed for a VM disk attachment.
 */
data class VirtualStorageLinkInfo(
		val link: VirtualStorageLink,
		val device: VirtualStorageDataCollection,
		val storageHost: HostDataCollection
)