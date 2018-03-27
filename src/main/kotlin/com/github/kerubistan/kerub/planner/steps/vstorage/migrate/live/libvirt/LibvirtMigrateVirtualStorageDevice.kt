package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.AbstractMigrateVirtualStorageDevice

data class LibvirtMigrateVirtualStorageDevice(val vm: VirtualMachine, val shallow : Boolean,
											  override val device: VirtualStorageDevice,
											  override val sourceAllocation: VirtualStorageAllocation,
											  override val targetAllocation: VirtualStorageAllocation,
											  override val source: Host, override val target: Host) :
		AbstractMigrateVirtualStorageDevice()