package com.github.kerubistan.kerub.services.exc.mappers

import org.junit.Test
import javax.ws.rs.core.Response
import kotlin.test.assertEquals

class DefaultExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = DefaultExceptionMapper().toResponse(NullPointerException("Surprise!"))
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.statusCode, response.status)
	}
}