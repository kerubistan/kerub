package com.github.K0zka.kerub.exceptions.mappers

import com.github.K0zka.kerub.security.mappers.RestError
import org.apache.sshd.common.RuntimeSshException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

public class RuntimeSshExceptionMapper : ExceptionMapper<RuntimeSshException> {
	override fun toResponse(exception: RuntimeSshException?): Response =
			Response.status(Response.Status.NOT_ACCEPTABLE).entity(RestError("HOST1", "Host could not be connected")).build()
}