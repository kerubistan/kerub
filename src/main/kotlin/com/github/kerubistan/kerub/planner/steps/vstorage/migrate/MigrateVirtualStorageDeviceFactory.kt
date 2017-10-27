package com.github.kerubistan.kerub.planner.steps.vstorage.migrate

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory

object MigrateVirtualStorageDeviceFactory : AbstractOperationalStepFactory<MigrateVirtualStorageDevice>() {
	override fun produce(state: OperationalState): List<MigrateVirtualStorageDevice> {
		return listOf()
	}
}