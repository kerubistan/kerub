package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.utils.join

/**
 * Groups together step factories that perform similar functionality, e.g. start VM, create storage
 */
open class StepFactoryCollection(
		private val factories: List<AbstractOperationalStepFactory<*>>
) : AbstractOperationalStepFactory<AbstractOperationalStep>() {
	override fun produce(state: OperationalState): List<AbstractOperationalStep>
			= factories.map { it.produce(state) }.join()
}