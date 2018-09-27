package com.github.kerubistan.kerub.services.exc.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.nhaarman.mockito_kotlin.mock
import org.apache.http.HttpStatus
import org.apache.sshd.common.RuntimeSshException
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import java.nio.channels.UnresolvedAddressException
import javax.ws.rs.core.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = DefaultExceptionMapper().toResponse(NullPointerException("Surprise!"))
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR.statusCode, response.status)
	}

	@Test
	fun toResponseWithJsonParseException() {
		val response = DefaultExceptionMapper().toResponse(JsonParseException("TEST", null))
		Assert.assertThat(response, CoreMatchers.notNullValue())
		Assert.assertThat(response.status, CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}

	@Test
	fun toResponseWithJsonMappingException() {
		val exception = mock<JsonMappingException>()
		val response = DefaultExceptionMapper().toResponse(exception)
		assertEquals(Response.Status.NOT_ACCEPTABLE.statusCode, response.status)
		assertNotNull(response.entity)
	}

	@Test
	fun toResponseWithRuntimeSshException() {
		val response = DefaultExceptionMapper().toResponse(RuntimeSshException("foo"))
		Assert.assertThat(response, CoreMatchers.notNullValue())
		Assert.assertEquals(response.status, Response.Status.NOT_ACCEPTABLE.statusCode)
		Assert.assertNotNull(response.entity)
	}

	@Test
	fun toResponseWithUnresolvedAddressException() {
		val response = DefaultExceptionMapper().toResponse(UnresolvedAddressException())
		Assert.assertEquals(response.status, Response.Status.NOT_ACCEPTABLE.statusCode)
		Assert.assertNotNull(response.entity)
	}

}