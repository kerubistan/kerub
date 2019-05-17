package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.planner.OperationalState
import io.github.kerubistan.kroki.collections.join

/**
 * Groups together step factories that perform similar functionality, e.g. start VM, create storage
 */
open class StepFactoryCollection(
		val factories: List<AbstractOperationalStepFactory<*>>,
		val enabled: (ControllerConfig) -> Boolean = { true }
) : AbstractOperationalStepFactory<AbstractOperationalStep>() {

	override val problemHints =  factories.map { it.problemHints }.join().toSet()
	override val expectationHints = factories.map { it.expectationHints }.join().toSet()

	override fun produce(state: OperationalState): List<AbstractOperationalStep> =
			factoryFeature(enabled(state.controllerConfig)) {
				factories.map {
					it.produce(state)
				}.join()
			}
}