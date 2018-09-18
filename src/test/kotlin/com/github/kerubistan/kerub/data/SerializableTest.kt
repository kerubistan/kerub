package com.github.kerubistan.kerub.data

import org.junit.Test
import org.reflections.Reflections
import java.io.Serializable

class SerializableTest {
	@Test
	fun checkSerializables() {
		//check that all serializable classes have only fields that are also serializable
		val subTypes = Reflections("com.github.kerubistan.kerub.model")
				.getSubTypesOf(Serializable::class.java)
				.filter { !it.isInterface
						!it.isEnum
								!it.isSynthetic
						&& it.name.startsWith("com.github.kerubistan.kerub.model") }
		for(type in subTypes) {
			println("$type - ")
		}
	}
}