package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep

data class CreateImage(val device : VirtualStorageDevice, val host : Host) : AbstractOperationalStep() {
	override fun take(state: OperationalState): OperationalState {
		throw UnsupportedOperationException()
	}
}