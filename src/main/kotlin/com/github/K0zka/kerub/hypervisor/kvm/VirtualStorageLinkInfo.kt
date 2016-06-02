package com.github.K0zka.kerub.hypervisor.kvm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageAllocation
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic

/**
 * Collection of all the technical data needed for a VM disk attachment.
 */
data class VirtualStorageLinkInfo(
		val link: VirtualStorageLink,
		val device:VirtualStorageDevice,
		val deviceDyn : VirtualStorageDeviceDynamic,
		val host: Host,
		val hostDynamic: HostDynamic
)