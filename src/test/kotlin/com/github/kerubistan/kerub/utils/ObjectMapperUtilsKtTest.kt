package com.github.kerubistan.kerub.utils

import com.fasterxml.jackson.databind.exc.IgnoredPropertyException
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kerubistan.kerub.model.ProjectMembership
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.planner.costs.TimeCost
import com.github.kerubistan.kerub.testVm
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ObjectMapperUtilsKtTest {
	@Test
	fun serialization() {
		val mapper = createObjectMapper()
		val serialized = mapper.writeValueAsString(testVm)
		val deserialized = mapper.readValue(serialized, VirtualMachine::class.java)
		val againSerialized = mapper.writeValueAsString(deserialized)
		val againDeserialized = mapper.readValue(againSerialized, VirtualMachine::class.java)
		assertEquals(testVm, deserialized)
	}

	@Test
	fun deserializationSettings() {
		assertThrows<InvalidFormatException>("enum as number not allowed") {
			createObjectMapper().readValue<HostDynamic>("""
			{
				"@type": "host-dyn",
				"id": "${UUID.randomUUID()}",
				"status": 1
			}
		""".trimIndent())
		}

		assertThrows<UnrecognizedPropertyException>("no random properties") {
			createObjectMapper().readValue<HostDynamic>("""
			{
				"@type": "host-dyn",
				"id": "${UUID.randomUUID()}",
				"status": "Up",
				"blah-${UUID.randomUUID()}": "yes please"
			}
		""".trimIndent())
		}

		assertThrows<IgnoredPropertyException>("no ignored properties") {
			createObjectMapper().readValue<ProjectMembership>("""
			{
				"@type": "project-membership",
				"id":"${UUID.randomUUID()}",
				"groupId":"${UUID.randomUUID()}",
				"user":"eugene",
				"groupIdStr" : "${UUID.randomUUID()}"
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


}