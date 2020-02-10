package com.github.kerubistan.kerub.services.exc.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.github.kerubistan.kerub.services.exc.HostAddressException
import com.github.kerubistan.kerub.utils.getLogger
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.sshd.common.RuntimeSshException
import org.apache.sshd.common.SshException
import java.lang.reflect.UndeclaredThrowableException
import java.nio.channels.UnresolvedAddressException
import java.util.UUID.randomUUID
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.FORBIDDEN
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE
import javax.ws.rs.core.Response.Status.UNAUTHORIZED
import javax.ws.rs.ext.ExceptionMapper

class DefaultExceptionMapper : ExceptionMapper<Throwable> {

	companion object {
		private val logger = getLogger()
		val jsonMappingError = RestError("MAP1", "Mapping error.")
		val jsonParseError = RestError("MAP2", "Json parsing error")
	}

	private fun Response.ResponseBuilder.contentType(contentType: String) = this.header("Content-Type", contentType)

	override fun toResponse(exception: Throwable): Response {
		val id = randomUUID()
		logger.info("exception {}", id, exception)
		return when (exception) {
			is UnresolvedAddressException ->
				Response.status(NOT_ACCEPTABLE)
						.entity(RestError("HOST2", "Address can not be resolved", id))
						.build()
			is UnauthenticatedException ->
				Response.status(UNAUTHORIZED)
						.entity(RestError("AUTH1", "Authentication needed", id))
						.build()
			is RuntimeSshException ->
				Response.status(NOT_ACCEPTABLE)
						.entity(RestError("HOST1", "Host could not be connected", id))
						.build()
			is UndeclaredThrowableException ->
				toResponse(exception.undeclaredThrowable)
			is HostAddressException ->
				Response.status(NOT_ACCEPTABLE)
						.entity(RestError("HOST2", "Host address not valid", id))
						.build()
			is JsonMappingException ->
				Response.status(NOT_ACCEPTABLE)
						.entity(jsonMappingError.copy(
								message = """${jsonMappingError.message}\n
												|path: ${exception.pathReference}\n
												|message: ${exception.message}""".trimMargin(),
								uid = id
						)
						)
						.contentType(APPLICATION_JSON)
						.build()
			is JsonParseException ->
				Response.status(NOT_ACCEPTABLE)
						.entity(jsonParseError.copy(uid = id))
						.contentType(APPLICATION_JSON)
						.build()
			is AuthenticationException ->
				Response.status(UNAUTHORIZED)
						.entity(RestError("AUTH2", "Authentication needed", id))
						.build()
			is AuthorizationException ->
				Response.status(FORBIDDEN)
						.entity(RestError("SEC1", "Access denied", id))
						.build()
			is SshException ->
				Response.status(NOT_ACCEPTABLE)
						.entity(RestError(
								code = "HOST3",
								message = exception.message,
								uid = id
						))
						.contentType(APPLICATION_JSON)
						.build()
			else ->
				Response.status(INTERNAL_SERVER_ERROR)
						.entity(RestError("INT1", "Internal error.", uid = id))
						.build()

		}

	}
}