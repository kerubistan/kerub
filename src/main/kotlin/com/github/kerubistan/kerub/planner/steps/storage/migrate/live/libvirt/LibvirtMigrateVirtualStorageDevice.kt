package com.github.kerubistan.kerub.planner.steps.storage.migrate.live.libvirt

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.storage.migrate.AbstractMigrateVirtualStorageDevice
import java.util.UUID

data class LibvirtMigrateVirtualStorageDevice(val vm: VirtualMachine, val shallow : Boolean,
											  override val device: VirtualStorageDevice,
											  override val sourceAllocation: VirtualStorageAllocation,
											  override val targetAllocation: VirtualStorageAllocation,
											  override val source: Host,
											  override val target: Host,
											  override val targetStorageCapabilityId: UUID) :
		AbstractMigrateVirtualStorageDevice()