package com.github.kerubistan.kerub.jackson

import com.fasterxml.jackson.databind.exc.IgnoredPropertyException
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kerubistan.kerub.model.ProjectMembership
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JacksonFuckIT {

	data class Something(val listOfNotNulls: List<String> = listOf())

	data class DataWithListOfInts(val listOfNotNulls: List<Int> = listOf())

	data class DataWithMap(val map: Map<String, Int> = mapOf())

	@Test
	fun nullThroughJackson() {
		val something = createObjectMapper()
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

		// TODO https://github.com/kerubistan/kerub/issues/205 - nulls injected through json
		// this should not work either, but it does
		createObjectMapper()
				.readValue("""
				{
					"map": {"A":1, "B":null}
				}
				""".trimIndent(), DataWithMap::class.java)

		// TODO https://github.com/kerubistan/kerub/issues/205 - nulls injected through json
		// and again, this is a wtf
		createObjectMapper()
				.readValue("""
				{
					"map": {"A":1, "B":""}
				}
				""".trimIndent(), DataWithMap::class.java)

	}

	@Test
	fun deserializationSettings() {
		assertThrows<InvalidFormatException>("enum as number not allowed") {
			createObjectMapper().readValue<HostDynamic>("""
			{
				"@type": "host-dyn",
				"id": "${randomUUID()}",
				"status": 1
			}
		""".trimIndent())
		}

		assertThrows<UnrecognizedPropertyException>("no random properties") {
			createObjectMapper().readValue<HostDynamic>("""
			{
				"@type": "host-dyn",
				"id": "${randomUUID()}",
				"status": "Up",
				"blah-${randomUUID()}": "yes please"
			}
		""".trimIndent())
		}

		assertThrows<IgnoredPropertyException>("no ignored properties") {
			createObjectMapper().readValue<ProjectMembership>("""
			{
				"@type": "project-membership",
				"id":"${randomUUID()}",
				"groupId":"${randomUUID()}",
				"user":"eugene",
				"groupIdStr" : "${randomUUID()}"
			}
		""".trimIndent())
		}

		assertThrows<MismatchedInputException>("primitives must not be nulls") {
			createObjectMapper().readValue<TimeCost>("""
			{
				"minMs": null,
				"maxMs": null
			}
		""".trimIndent())
		}

		assertThrows<InvalidDefinitionException>("test that we do not bypass the validation") {
			createObjectMapper().readValue<TimeCost>("""
			{
				"minMs": 1000,
				"maxMs": 100
			}
		""".trimIndent())
		}

	}

	@Ignore
	@Test
	fun otherProblems() {
		// TODO this too should fail, but it does not
		assertThrows<InvalidFormatException>("duplicate keys not allowed") {
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