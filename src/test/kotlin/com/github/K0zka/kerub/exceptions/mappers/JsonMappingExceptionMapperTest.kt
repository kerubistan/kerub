package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.databind.JsonMappingException
import com.github.K0zka.kerub.exceptions.mappers.RestError
import com.github.K0zka.kerub.utils.createObjectMapper
import org.junit.Assert
import org.junit.Test

public class JsonMappingExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = JsonMappingExceptionMapper(createObjectMapper()) toResponse JsonMappingException("TEST")
		Assert.assertNotNull(response)
		Assert.assertNotNull(response!!.getEntity())
	}
}