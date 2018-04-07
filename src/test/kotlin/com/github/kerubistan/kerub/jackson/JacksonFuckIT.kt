package com.github.kerubistan.kerub.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JacksonFuckIT {

	data class Something(val listOfNotNulls: List<String> = listOf())

	@Test
	fun nullThroughJackson() {
		val something = ObjectMapper().registerModule(KotlinModule())
				.readValue("""
					{
						"listOfNotNulls":["foo","bar",null,"baz"]
					}
					""".trimIndent(), Something::class.java)
		assertNotNull(something)
		// TODO https://github.com/kerubistan/kerub/issues/205 - nulls injected through json
		// this here is obviiously wrong and should fail instead
		assertTrue(something.listOfNotNulls.any { it == null })
		assertEquals(4, something.listOfNotNulls.size)
	}
}