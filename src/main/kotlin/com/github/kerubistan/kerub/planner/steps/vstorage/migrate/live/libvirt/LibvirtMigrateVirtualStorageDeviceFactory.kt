package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.AbstractMigrateVirtualStorageDevice

object LibvirtMigrateVirtualStorageDeviceFactory :
		AbstractOperationalStepFactory<AbstractMigrateVirtualStorageDevice>() {
	override fun produce(state: OperationalState): List<AbstractMigrateVirtualStorageDevice>
			= TODO()
}