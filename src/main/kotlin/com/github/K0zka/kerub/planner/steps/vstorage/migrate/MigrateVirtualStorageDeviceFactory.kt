package com.github.K0zka.kerub.planner.steps.vstorage.migrate

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object MigrateVirtualStorageDeviceFactory : AbstractOperationalStepFactory<MigrateVirtualStorageDevice>() {
	override fun produce(state: OperationalState): List<MigrateVirtualStorageDevice> {
		return listOf()
	}
}