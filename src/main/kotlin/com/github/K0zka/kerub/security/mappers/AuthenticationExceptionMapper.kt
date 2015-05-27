package com.github.K0zka.kerub.security.mappers

import javax.ws.rs.ext.ExceptionMapper
import org.apache.shiro.authc.AuthenticationException
import com.github.K0zka.kerub.utils.getLogger
import org.apache.shiro.authz.UnauthenticatedException
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

public class AuthenticationExceptionMapper : ExceptionMapper<AuthenticationException>{
	companion object {
		private val logger = getLogger(AuthenticationExceptionMapper::class)
	}
	override fun toResponse(exception: AuthenticationException?): Response? {
		logger.debug("Not authenticated", exception)
		return Response.status(Status.UNAUTHORIZED).entity(RestError("AUTH2", "Authentication needed")).build()
	}

}