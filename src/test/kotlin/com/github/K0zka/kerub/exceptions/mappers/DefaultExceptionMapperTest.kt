package com.github.K0zka.kerub.exceptions.mappers

import org.junit.Test

public class DefaultExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = DefaultExceptionMapper().toResponse(NullPointerException("Surprise!"))

	}
}