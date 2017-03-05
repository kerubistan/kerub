package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.databind.JsonMappingException
import com.github.K0zka.kerub.utils.createObjectMapper
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import javax.ws.rs.core.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JsonMappingExceptionMapperTest {
	@Test
	fun toResponse() {
		val exception = mock<JsonMappingException>()
		val response = JsonMappingExceptionMapper(createObjectMapper()).toResponse(exception)
		assertEquals(Response.Status.NOT_ACCEPTABLE.statusCode, response!!.status)
		assertNotNull(response.entity)
	}
}