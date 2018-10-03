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
						val annotation = stepClass.kotlin.annotations.filterIsInstance<ProducedBy>().singleOrNull()
						if(annotation == null) {
							val factoryClass = Class.forName(stepClass.name + "Factory")
							assertTrue ("$stepClass needs a factory or a @ProducedBy annotation") {
								AbstractOperationalStepFactory::class.java.isAssignableFrom(factoryClass)
							}
							validateFactory(factoryClass)
						} else {
							validateFactory(annotation.factory.java)
						}
					}
				}

	}

	private fun validateFactory(factoryClass: Class<*>) {
		assertTrue("$factoryClass must be an object") {
			factoryClass.kotlin.objectInstance != null
		}
	}

}