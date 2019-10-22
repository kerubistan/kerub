package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat

data class CreateImage(
		override val disk: VirtualStorageDevice,
		override val capability: FsStorageCapability,
		override val host: Host,
		override val format: VirtualDiskFormat
) : AbstractCreateImage() {

	init {
		require(host.capabilities?.storageCapabilities?.contains(capability) ?: false) {
			"host (${host.id}) capabilities (${host.capabilities?.storageCapabilities}) must include ${capability.id}"
		}
	}

	@get:JsonIgnore
	override val allocation: VirtualStorageFsAllocation by lazy {
		VirtualStorageFsAllocation(
				hostId = host.id,
				actualSize = disk.size, //TODO not true when thin provisioning
				mountPoint = path,
				type = format,
				fileName = "$path/${disk.id}.${format}",
				capabilityId = capability.id
		)
	}


}