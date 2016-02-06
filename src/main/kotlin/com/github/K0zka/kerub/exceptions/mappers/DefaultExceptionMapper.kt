package com.github.K0zka.kerub.exceptions.mappers

import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class DefaultExceptionMapper : ExceptionMapper<Throwable> {
	override fun toResponse(exception: Throwable): Response {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(RestError("INT1", "Internal error.")).build()
	}
}