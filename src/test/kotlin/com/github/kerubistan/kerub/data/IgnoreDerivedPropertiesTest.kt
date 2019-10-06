package com.github.kerubistan.kerub.data

import org.junit.Test
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import kotlin.reflect.full.declaredMemberProperties

class IgnoreDerivedPropertiesTest {
	@Test
	fun check() {
		val reflections = Reflections("com.github.kerubistan.kerub.model", SubTypesScanner(false))
		reflections.allTypes.map { Class.forName(it).kotlin }
				.filterNot {
					(it.java.name?.contains("Test") ?: true)
							|| (it.java.name?.contains("DefaultImpls") ?: true)
							|| (it.java.name?.contains("Companion") ?: true)
							|| (it.java.name?.endsWith("Kt") ?: true)
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