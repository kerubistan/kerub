package com.github.kerubistan.kerub.data

import org.junit.Test
import org.reflections.Reflections
import java.io.Serializable
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.jvmErasure
import kotlin.test.assertTrue

class SerializableTest {
	@Test
	fun checkSerializables() {
		//check that all serializable classes have only fields that are also serializable
		val subTypes = Reflections("com.github.kerubistan.kerub.model")
				.getSubTypesOf(Serializable::class.java)
				.filter {
					!it.isInterface
							&& !it.isEnum
							&& !it.isSynthetic
							&& it.name.startsWith("com.github.kerubistan.kerub.model")
							&& !it.name.contains("$")
				}.forEach { type ->
					assertTrue("Hey ${type.kotlin.qualifiedName} should be data class") { type.kotlin.isData }
					type.kotlin.memberProperties.forEach { prop ->
						assertTrue(
								"${type.kotlin.qualifiedName}.${prop.name} \t ${prop.returnType.classifier} " +
										"should be serializable") {
							prop.returnType.jvmErasure.isSubclassOf(Serializable::class) ||
									if (prop.returnType.javaType is Class<*>) {
										Serializable::class.java.isAssignableFrom(
												prop.returnType.javaType as Class<*>
										)
									} else {
										true
									}
						}
					}
				}
	}
}