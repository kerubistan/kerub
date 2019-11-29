package com.github.kerubistan.kerub.model

import com.github.kerubistan.kerub.assertSerializeNicely
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.jgroups.util.Util.assertEquals
import org.junit.Test

abstract class AbstractDataRepresentationTest<T : Any> {

	abstract val testInstances : Collection<T>
	abstract val clazz : Class<T>

	@Test
	fun checkJavaSerialization() {
		testInstances.forEach(::assertSerializeNicely)
	}

	@Test
	fun checkJsonSerialization() {
		val objectMapper = createObjectMapper(prettyPrint = true)
		testInstances.forEach { instance ->
			val serialized = objectMapper.writeValueAsString(instance)

			val deserialized = objectMapper.readValue(serialized, clazz)

			assertEquals(instance, deserialized)
		}
	}

}