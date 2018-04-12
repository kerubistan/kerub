package com.github.kerubistan.kerub.services.exc.mappers

import org.junit.Assert
import org.junit.Test
import java.nio.channels.UnresolvedAddressException
import javax.ws.rs.core.Response

class UnresolvedAddressExceptionMapperTest {
	@Test
	fun toResponse() {
		val response = UnresolvedAddressExceptionMapper().toResponse(UnresolvedAddressException())
		Assert.assertEquals(response.status, Response.Status.NOT_ACCEPTABLE.statusCode)
		Assert.assertNotNull(response.entity)
	}
}