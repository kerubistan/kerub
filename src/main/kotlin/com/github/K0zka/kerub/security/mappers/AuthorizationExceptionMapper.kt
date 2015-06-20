package com.github.K0zka.kerub.security.mappers

import com.github.K0zka.kerub.utils.getLogger
import org.apache.shiro.authz.AuthorizationException
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.ext.ExceptionMapper

public class AuthorizationExceptionMapper : ExceptionMapper<AuthorizationException> {

	companion object {
		private val logger = getLogger(AuthorizationExceptionMapper::class)
	}

	override fun toResponse(exception: AuthorizationException?): Response {
		logger.debug("Authorization denied", exception)
		return Response.status(Status.FORBIDDEN).entity(RestError("SEC1", "Access denied")).build()
	}
}