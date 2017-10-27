package com.github.kerubistan.kerub.exceptions.mappers

import com.github.kerubistan.kerub.utils.getLogger
import org.apache.shiro.authc.AuthenticationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class AuthenticationExceptionMapper : ExceptionMapper<AuthenticationException> {
	companion object {
		private val logger = getLogger(AuthenticationExceptionMapper::class)
	}

	override fun toResponse(exception: AuthenticationException): Response {
		logger.debug("Not authenticated", exception)
		return Response.status(Response.Status.UNAUTHORIZED).entity(RestError("AUTH2", "Authentication needed")).build()
	}

}