package com.github.kerubistan.kerub.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JacksonFuckIT {

	data class Something(val listOfNotNulls: List<String> = listOf())

	data class DataWithListOfInts(val listOfNotNulls: List<Int> = listOf())

	data class DataWithMap(val map: Map<String, Int> = mapOf())

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

	@Test
	fun invalidThroughJackson() {
		// at least this one is good, we can't inject invalid values
		assertThrows<InvalidFormatException>("string in an int array") {
			ObjectMapper().registerModule(KotlinModule())
					.readValue("""
					{
						"listOfNotNulls":[1, 2,"surprise"]
					}
					""".trimIndent(), DataWithListOfInts::class.java)
		}

		//this is also good
		assertThrows<InvalidFormatException>("string as value in map") {
			ObjectMapper().registerModule(KotlinModule())
					.readValue("""
					{
						"map": {"A":1, "B":"surprise"}
					}
					""".trimIndent(), DataWithMap::class.java)
		}

		// TODO https://github.com/kerubistan/kerub/issues/205 - nulls injected through json
		// this should not work either, but it does
		ObjectMapper().registerModule(KotlinModule())
				.readValue("""
				{
					"map": {"A":1, "B":null}
				}
				""".trimIndent(), DataWithMap::class.java)

		// TODO https://github.com/kerubistan/kerub/issues/205 - nulls injected through json
		// and again, this is a wtf
		ObjectMapper().registerModule(KotlinModule())
				.readValue("""
				{
					"map": {"A":1, "B":""}
				}
				""".trimIndent(), DataWithMap::class.java)

	}

}