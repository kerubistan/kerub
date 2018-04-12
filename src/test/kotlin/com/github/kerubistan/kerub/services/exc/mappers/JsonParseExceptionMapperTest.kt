package com.github.kerubistan.kerub.services.exc.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.github.kerubistan.kerub.utils.createObjectMapper
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class JsonParseExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = JsonParseExceptionMapper(createObjectMapper()).toResponse(JsonParseException("TEST", null))
		Assert.assertThat(response, CoreMatchers.notNullValue())
		Assert.assertThat(response!!.status, CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}
}