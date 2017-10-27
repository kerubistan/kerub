package com.github.kerubistan.kerub.exceptions.mappers

import com.github.kerubistan.kerub.exc.HostAddressException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class HostAddressExceptionMapper : ExceptionMapper<HostAddressException> {
	override fun toResponse(exception: HostAddressException?): Response =
			Response.status(Response.Status.NOT_ACCEPTABLE).entity(RestError("HOST2", "Host address not valid")).build()
}