package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.github.K0zka.kerub.utils.createObjectMapper
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

public class JsonParseExceptionMapperTest {
	Test
	fun toResponse() {
		val response = JsonParseExceptionMapper(createObjectMapper()).toResponse(JsonParseException("TEST", null))
		Assert.assertThat(response, CoreMatchers.notNullValue())
		Assert.assertThat(response!!.getStatus(), CoreMatchers.`is`(HttpStatus.SC_NOT_ACCEPTABLE))
	}
}