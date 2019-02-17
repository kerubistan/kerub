package com.github.kerubistan.kerub.services.exc.mappers

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.github.kerubistan.kerub.services.exc.HostAddressException
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthenticatedException
import org.apache.sshd.common.RuntimeSshException
import org.apache.sshd.common.SshException
import java.lang.reflect.UndeclaredThrowableException
import java.nio.channels.UnresolvedAddressException
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.FORBIDDEN
import javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import javax.ws.rs.core.Response.Status.NOT_ACCEPTABLE
import javax.ws.rs.core.Response.Status.UNAUTHORIZED
import javax.ws.rs.ext.ExceptionMapper

class DefaultExceptionMapper : ExceptionMapper<Throwable> {

	companion object {
		val jsonMappingError = RestError("MAP1", "Mapping error.")
		val jsonParseError = RestError("MAP2", "Json parsing error")
	}

	private fun Response.ResponseBuilder.contentType(contentType: String) = this.header("Content-Type", contentType)

	override fun toResponse(exception: Throwable): Response =
			when (exception) {
				is UnresolvedAddressException ->
					Response.status(NOT_ACCEPTABLE)
							.entity(RestError("HOST2", "Address can not be resolved"))
							.build()
				is UnauthenticatedException ->
					Response.status(UNAUTHORIZED)
							.entity(RestError("AUTH1", "Authentication needed"))
							.build()
				is RuntimeSshException ->
					Response.status(NOT_ACCEPTABLE)
							.entity(RestError("HOST1", "Host could not be connected"))
							.build()
				is UndeclaredThrowableException ->
					toResponse(exception.undeclaredThrowable)
				is HostAddressException ->
					Response.status(NOT_ACCEPTABLE)
							.entity(RestError("HOST2", "Host address not valid"))
							.build()
				is JsonMappingException ->
					Response.status(NOT_ACCEPTABLE)
							.entity(jsonMappingError.
									copy(
											message = """${jsonMappingError.message}\n
												|path: ${exception.pathReference}\n
												|message: ${exception.message}""".trimMargin()
									)
							)
							.contentType(APPLICATION_JSON)
							.build()
				is JsonParseException ->
					Response.status(NOT_ACCEPTABLE)
							.entity(jsonParseError)
							.contentType(APPLICATION_JSON)
							.build()
				is AuthenticationException ->
					Response.status(UNAUTHORIZED)
							.entity(RestError("AUTH2", "Authentication needed"))
							.build()
				is AuthorizationException ->
					Response.status(FORBIDDEN)
							.entity(RestError("SEC1", "Access denied"))
							.build()
				is SshException ->
					Response.status(NOT_ACCEPTABLE)
							.entity(RestError(
											code = "HOST3",
											message = exception.message
									))
							.contentType(APPLICATION_JSON)
							.build()
				else ->
					Response.status(INTERNAL_SERVER_ERROR)
							.entity(RestError("INT1", "Internal error."))
							.build()

			}
}