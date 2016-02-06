package com.github.K0zka.kerub.exceptions.mappers

import org.apache.sshd.common.RuntimeSshException
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import javax.ws.rs.core.Response

class RuntimeSshExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = RuntimeSshExceptionMapper().toResponse(RuntimeSshException("foo"))
		Assert.assertThat(response, CoreMatchers.notNullValue())
		Assert.assertEquals(response.status, Response.Status.NOT_ACCEPTABLE.statusCode)
		Assert.assertNotNull(response.entity)
	}
}