package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

/**
 *
 */
object CreateGvinumVolumeFactory : AbstractOperationalStepFactory<CreateGvinumVolume>() {
	override fun produce(state: OperationalState): List<CreateGvinumVolume> {
		state.hosts.filter { it.value.capabilities?.os == OperatingSystem.BSD }
		TODO()
	}
}