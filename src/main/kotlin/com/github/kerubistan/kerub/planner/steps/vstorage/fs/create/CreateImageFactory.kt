package com.github.kerubistan.kerub.planner.steps.vstorage.fs.create

import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.base.AbstractCreateFileVirtualStorageFactory
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg

object CreateImageFactory : AbstractCreateFileVirtualStorageFactory<CreateImage>() {
	override val requiredOsCommand: OsCommand
		get() = QemuImg

	override
	fun createStep(storage: VirtualStorageDevice, hostData: HostDataCollection, mount: FsStorageCapability): CreateImage =
			CreateImage(
					disk = storage,
					host = hostData.stat,
					format = (storage.expectations.firstOrNull { it is StorageAvailabilityExpectation }
							as StorageAvailabilityExpectation?)?.format
							?: VirtualDiskFormat.qcow2,
					path = mount.mountPoint
			)

}