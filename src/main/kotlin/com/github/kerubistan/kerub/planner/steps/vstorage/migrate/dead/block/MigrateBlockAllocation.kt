package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.dead.block

import com.github.kerubistan.kerub.model.CompressionFormat
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.base.UnAllocate
import com.github.kerubistan.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.dead.AbstractMigrateAllocation

data class MigrateBlockAllocation(
		override val sourceHost: Host,
		override val targetHost: Host,
		val compression: CompressionFormat? = null,
		override val virtualStorage: VirtualStorageDevice,
		override val sourceAllocation: VirtualStorageBlockDeviceAllocation,
		override val allocationStep: AbstractCreateVirtualStorage<out VirtualStorageBlockDeviceAllocation, out StorageCapability>,
		override val deAllocationStep: UnAllocate<*>
) : AbstractMigrateAllocation() {

	init {
		validate()
	}

	override fun validate() {
		super.validate()
		if (compression != null) {
			fun checkHost(host: Host, name: String) {
				checkNotNull(host.capabilities)
				check(compression in host.capabilities.compressionCapabilities) {
					"Compression $compression not in $name host (${host.id} ${host.address}) capabilities " +
							"(${host.capabilities.compressionCapabilities})"
				}
			}
			checkHost(sourceHost, "source")
			checkHost(targetHost, "target")
		}
		check(allocationStep.format == VirtualDiskFormat.raw) {
			"Allocation step $allocationStep format (${allocationStep.format}) is not raw"
		}
	}

	override fun reservations(): List<Reservation<*>> = allocationStep.reservations() +
			deAllocationStep.reservations()

}