package com.github.kerubistan.kerub.services.exc.mappers

import com.github.kerubistan.kerub.utils.getLogger
import org.apache.shiro.authz.UnauthenticatedException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class UnauthenticatedExceptionMapper : ExceptionMapper<UnauthenticatedException> {
	companion object {
		private val logger = getLogger(UnauthenticatedExceptionMapper::class)
	}

	override fun toResponse(exception: UnauthenticatedException): Response {
		logger.debug("Not authenticated", exception)
		return Response.status(Response.Status.UNAUTHORIZED).entity(RestError("AUTH1", "Authentication needed")).build()
	}
}