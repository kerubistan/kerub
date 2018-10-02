package com.github.kerubistan.kerub.planner.steps

import org.junit.Test
import org.reflections.Reflections
import kotlin.test.assertTrue

class AbstractOperationalStepTest {

	@Test
	fun validate() {
		//each subclass of AbstractOperationalStep must have a factory with name "${stepClass}Factory"
		//or have a ProducedBy annotation

		Reflections("com.github.kerubistan.kerub.planner.steps")
				.getSubTypesOf(AbstractOperationalStep::class.java).forEach {
					stepClass ->
					if(!stepClass.kotlin.isAbstract) {
						val annotation = stepClass.kotlin.annotations.singleOrNull { it is ProducedBy }
						if(annotation == null) {
							val factoryClass = Class.forName(stepClass.name + "Factory")
							assertTrue ("$stepClass needs a factory or a @ProducedBy annotation") {
								AbstractOperationalStepFactory::class.java.isAssignableFrom(factoryClass)
							}
						}
					}
				}

	}

}