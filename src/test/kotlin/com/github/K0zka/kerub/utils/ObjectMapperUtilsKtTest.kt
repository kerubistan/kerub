package com.github.K0zka.kerub.utils

import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.testVm
import org.junit.Assert.assertEquals
import org.junit.Test

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

}