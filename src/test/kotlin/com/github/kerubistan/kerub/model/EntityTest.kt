package com.github.kerubistan.kerub.model

import org.junit.Test
import org.reflections.Reflections
import kotlin.test.assertTrue

class EntityTest {
	@Test
	fun checkAllEntitiesAreData() {
		Reflections("com.github.kerubistan.kerub")
				.getSubTypesOf(Entity::class.java).forEach {
					assertTrue("${it.name} must be data class") {
						it.isInterface || it.kotlin.isAbstract || it.kotlin.isData
					}
				}
	}

}