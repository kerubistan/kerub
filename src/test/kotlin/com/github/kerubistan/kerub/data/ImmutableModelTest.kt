package com.github.kerubistan.kerub.data

import org.apache.commons.beanutils.PropertyUtils
import org.junit.Test
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.test.assertTrue

class ImmutableModelTest {
	// check that model classes are immutables (no setters ever)
	@Test
	fun check() {
		val reflections = Reflections("com.github.kerubistan.kerub.model", SubTypesScanner(false))
		reflections.allTypes.map { Class.forName(it).kotlin }
				.filter { !(it.java.name?.contains("Test") ?: true) }
				.forEach {clzz ->
					PropertyUtils.getPropertyDescriptors(clzz.java).forEach { prop ->
						assertTrue("${clzz.qualifiedName}/${prop.name} should be immutable") {
							prop.writeMethod == null
						}
					}
				}
	}
}