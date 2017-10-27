package com.github.kerubistan.kerub.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ObjectMapperFactoryTest {

	data class Sample(
			val name : String,
			val tags : List<String> = listOf()
	)

	@Test
	fun create() {
		val objectMapper = createObjectMapper(true)
		val sample = Sample(name = "foo", tags = listOf("foo", "bar"))
		assertEquals(sample, objectMapper.readValue(objectMapper.writeValueAsString(sample), Sample::class.java))
	}

}