package com.github.K0zka.kerub.security.mappers

import javax.ws.rs.ext.ExceptionMapper
import org.apache.shiro.authz.UnauthenticatedException
import javax.ws.rs.core.Response
import com.github.K0zka.kerub.utils.getLogger
import javax.ws.rs.core.Response.Status

public class UnauthenticatedExceptionMapper : ExceptionMapper<UnauthenticatedException> {
	class object {
		val logger = getLogger(javaClass<UnauthenticatedExceptionMapper>())
	}
	override fun toResponse(exception: UnauthenticatedException?): Response? {
		logger.debug("Not authenticated", exception)
		return Response.status(Status.UNAUTHORIZED).entity(RestError("AUTH1", "Authentication needed")).build()
	}
}