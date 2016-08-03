package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.utils.join

/**
 * Groups together step factories that perform similar functionality, e.g. start VM, create storage
 */
abstract class AbstractStepFactoryCollection(
		private val factories: List<AbstractOperationalStepFactory<*>>
) : AbstractOperationalStepFactory<AbstractOperationalStep>() {
	override fun produce(state: OperationalState): List<AbstractOperationalStep>
			= factories.map { it.produce(state) }.join()
}