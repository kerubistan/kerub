package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.virt.virsh.Virsh

class LibvirtMigrateVirtualStorageDeviceExecutor(private val hostCommandExecutor: HostCommandExecutor) :
		AbstractStepExecutor<LibvirtMigrateVirtualStorageDevice, Unit>() {
	override fun perform(step: LibvirtMigrateVirtualStorageDevice) {
		hostCommandExecutor.execute(step.source) {
			Virsh.blockCopy(session = it,
							blockDev = step.targetAllocation is VirtualStorageBlockDeviceAllocation,
							destination = TODO(),
							format = if (step.targetAllocation is VirtualStorageFsAllocation) {
								step.targetAllocation.type.name
							} else {
								null
							},
							path = TODO(),
							domaindId = step.vm.id)
		}
	}

	override fun update(step: LibvirtMigrateVirtualStorageDevice, updates: Unit) {
		TODO()
	}
}