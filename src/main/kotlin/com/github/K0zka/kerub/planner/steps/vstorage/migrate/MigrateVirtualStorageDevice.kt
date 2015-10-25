package com.github.K0zka.kerub.planner.steps.vstorage.migrate

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

public class MigrateVirtualStorageDevice(
		val device: VirtualStorageDevice,
		val target: Host
                                        ) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}