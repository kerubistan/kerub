package com.github.kerubistan.kerub.data

import org.junit.Test
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import kotlin.reflect.full.declaredMemberProperties

class IgnoreDerivedPropertiesTest {
	@Test
	fun check() {
		val reflections = Reflections("com.github.kerubistan.kerub.model", Scanners.SubTypes)
		reflections.getAll(Scanners.SubTypes).map { Class.forName(it).kotlin }
			.filterNot {
				(it.java.name.contains("Test"))
						|| (it.java.name.contains("DefaultImpls"))
						|| (it.java.name.contains("Companion"))
						|| (it.java.name.endsWith("Kt"))
			}
			.sortedBy { it.qualifiedName }
			.forEach { clzz ->
				println(clzz.qualifiedName)
				clzz.declaredMemberProperties.forEach {
					println("	- ${it.name}")
				}
			}
	}
}