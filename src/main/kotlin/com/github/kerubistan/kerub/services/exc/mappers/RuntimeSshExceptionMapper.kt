package com.github.kerubistan.kerub.services.exc.mappers

import org.apache.sshd.common.RuntimeSshException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class RuntimeSshExceptionMapper : ExceptionMapper<RuntimeSshException> {
	override fun toResponse(exception: RuntimeSshException): Response =
			Response.status(Response.Status.NOT_ACCEPTABLE).entity(RestError("HOST1", "Host could not be connected")).build()
}