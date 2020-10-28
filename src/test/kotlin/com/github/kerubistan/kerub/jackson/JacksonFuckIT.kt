package com.github.kerubistan.kerub.jackson

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

class JacksonFuckIT {

	data class Something(val listOfNotNulls: List<String> = listOf())

	data class DataWithListOfInts(val listOfNotNulls: List<Int> = listOf())

	data class DataWithMap(val map: Map<String, Int> = mapOf())

	@Test
	fun nullThroughJackson() {
		assertThrows<JsonMappingException> {
			val something = createObjectMapper()
					.readValue("""
					{
						"listOfNotNulls":["foo","bar",null,"baz"]
					}
					""".trimIndent(), Something::class.java)
		}
	}

	@Test
	fun invalidThroughJackson() {
		// at least this one is good, we can't inject invalid values
		assertThrows<InvalidFormatException>("string in an int array") {
			createObjectMapper()
					.readValue("""
					{
						"listOfNotNulls":[1, 2,"surprise"]
					}
					""".trimIndent(), DataWithListOfInts::class.java)
		}

		//this is also good
		assertThrows<InvalidFormatException>("string as value in map") {
			createObjectMapper()
					.readValue("""
					{
						"map": {"A":1, "B":"surprise"}
					}
					""".trimIndent(), DataWithMap::class.java)
		}

		assertThrows<JsonMappingException> {
			createObjectMapper()
					.readValue("""
				{
					"map": {"A":1, "B":null}
				}
				""".trimIndent(), DataWithMap::class.java)
		}

		assertThrows<JsonMappingException> {
			createObjectMapper()
					.readValue("""
				{
					"map": {"A":1, "B":""}
				}
				""".trimIndent(), DataWithMap::class.java)
		}

	}


	@Test
	fun otherProblems() {
		assertThrows<JsonParseException>("duplicate keys not allowed") {
			createObjectMapper().readValue<HostDynamic>("""
			{
				"@type": "host-dyn",
				"id": "${randomUUID()}",
				"id": "${randomUUID()}",
				"status": "Up",
				"status": "Down"
			}
		""".trimIndent())
		}

	}

}