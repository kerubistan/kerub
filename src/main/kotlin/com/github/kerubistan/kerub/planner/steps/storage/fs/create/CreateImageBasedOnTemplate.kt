package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.IOCost

@JsonTypeName("create-image-from-template")
data class CreateImageBasedOnTemplate(
		override val host: Host,
		override val disk: VirtualStorageDevice,
		override val capability: FsStorageCapability,
		override val format: VirtualDiskFormat,
		val baseAllocation : VirtualStorageFsAllocation,
		val baseDisk : VirtualStorageDevice
) : AbstractCreateImage() {

	@get:JsonIgnore
	override val allocation: VirtualStorageFsAllocation by lazy {
		VirtualStorageFsAllocation(
				hostId = host.id,
				actualSize = disk.size,
				mountPoint = path,
				type = format,
				fileName = "$path/${disk.id}.${format}",
				capabilityId = capability.id,
				backingFile = baseAllocation.fileName
		)
	}

	init {
		require(baseAllocation.hostId == allocation.hostId) {
			"base allocation ${baseAllocation.fileName} must be on the same host (${allocation.hostId})"
		}
		require(format in formatsWithBaseImage) {
			"format must be on of $formatsWithBaseImage"
		}
		require(format == baseAllocation.type) {
			"format of the new image ($format) must be the same as the format (${baseAllocation.type}) of the " +
					"backing allocation (${baseAllocation.hostId}/${baseAllocation.fileName})"
		}
		require(baseDisk.id != disk.id) {
			"template disk (${baseDisk.id}) must be different from the allocated disk (${disk.id})"
		}
	}

	override fun getCost(): List<Cost> {
		return super.getCost() + listOf(IOCost(bytes = requireNotNull(blankSize[allocation.type]), host = host))
	}
}