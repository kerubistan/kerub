package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage

data class CreateGvinumVolume(
		override val host: Host,
		override val disk: VirtualStorageDevice

) : AbstractCreateVirtualStorage {

	override fun take(state: OperationalState): OperationalState {
		TODO("not implemented")
	}
}