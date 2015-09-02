package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState

public object MigrateVirtualStorageDeviceFactory : AbstractOperationalStepFactory<MigrateVirtualStorageDevice>() {
	override fun produce(state: OperationalState): List<MigrateVirtualStorageDevice> {
		return listOf()
	}
}