package com.github.K0zka.kerub.security.mappers

import com.github.K0zka.kerub.utils.getLogger
import org.apache.shiro.authz.UnauthenticatedException
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.ext.ExceptionMapper

public class UnauthenticatedExceptionMapper : ExceptionMapper<UnauthenticatedException> {
	companion object {
		private val logger = getLogger(UnauthenticatedExceptionMapper::class)
	}
	override fun toResponse(exception: UnauthenticatedException?): Response? {
		logger.debug("Not authenticated", exception)
		return Response.status(Status.UNAUTHORIZED).entity(RestError("AUTH1", "Authentication needed")).build()
	}
}