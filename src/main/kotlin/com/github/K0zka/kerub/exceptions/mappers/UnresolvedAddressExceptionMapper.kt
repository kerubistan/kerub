package com.github.K0zka.kerub.exceptions.mappers

import java.nio.channels.UnresolvedAddressException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

public class UnresolvedAddressExceptionMapper : ExceptionMapper<UnresolvedAddressException> {
	override fun toResponse(exception: UnresolvedAddressException): Response =
			Response.status(Response.Status.NOT_ACCEPTABLE).entity(RestError("HOST2", "Address can not be resolved")).build()
}