package com.github.K0zka.kerub.planner.steps.vstorage.create

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory

public object CreateImageFactory : AbstractOperationalStepFactory<CreateImage>() {
	override fun produce(state: OperationalState): List<CreateImage> {
		throw UnsupportedOperationException()
	}
}