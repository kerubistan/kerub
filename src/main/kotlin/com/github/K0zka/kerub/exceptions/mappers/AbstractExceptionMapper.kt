package com.github.K0zka.kerub.exceptions.mappers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.K0zka.kerub.utils.getLogger
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

abstract class AbstractExceptionMapper<T : Throwable>(val mapper: ObjectMapper) : ExceptionMapper<T> {

	companion object {
		val logger = getLogger(AbstractExceptionMapper::class)
	}

	abstract fun getRestError(exception: T): RestError

	final override fun toResponse(exception: T): Response? {
		logger.warn("mapping error", exception)
		return Response.status(Response.Status.NOT_ACCEPTABLE).entity(
				mapper.writeValueAsString(RestError("MAP1", "Mapping failed"))
		)
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.build()

	}
}