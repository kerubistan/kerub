package com.github.K0zka.kerub

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.K0zka.kerub.exceptions.mappers.RestError
import com.github.K0zka.kerub.services.LoginService
import com.github.K0zka.kerub.services.getServiceBaseUrl
import com.github.K0zka.kerub.utils.createObjectMapper
import com.github.K0zka.kerub.utils.silent
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper
import org.apache.cxf.jaxrs.client.WebClient
import java.io.InputStream
import javax.ws.rs.core.Response
import kotlin.reflect.KClass

val testBaseUrl = "http://localhost:${System.getProperty("kerub.it.port") ?: "8080"}/"
val testRestBaseUrl = "${testBaseUrl}s/r"
val testWsUrl = "ws://localhost:${System.getProperty("kerub.it.port") ?: "8080"}/ws"

val testUsers = mapOf(
		"admin" to ("admin" to "password"),
		"user" to ("enduser" to "password")
		)

class RestException(val msg : String, val code : String, val status : Int, val response : Response) : RuntimeException()

class RestExceptionHandler(val objectMapper : ObjectMapper) : ResponseExceptionMapper<Exception> {
	override fun fromResponse(r: Response): Exception {
		val entity = silent { objectMapper.readValue(r.entity as InputStream, RestError::class.java) }
		return RestException(entity?.message ?: "" ,entity?.code ?: "", r.status, r)
	}
}

fun WebClient.login(username: String = "admin", password: String = "password") : Response =
	this.runRestAction(LoginService::class) {
		it.login(
				LoginService.UsernamePassword(username, password)
		)
		WebClient.client(it).response
	}

fun <X : Any, Y : Any> WebClient.runRestAction(clientClass: KClass<X>, action: (X) -> Y) =
	action(JAXRSClientFactory.fromClient(this, clientClass.java))


fun createClient() : WebClient {
	val objectMapper = createObjectMapper()
	val client = WebClient.create(getServiceBaseUrl(), listOf(JacksonJsonProvider(objectMapper), RestExceptionHandler(objectMapper)), true)
	WebClient.getConfig(client).requestContext.put(
			org.apache.cxf.message.Message.MAINTAIN_SESSION, true)
	return client
}

fun <T : Any> createServiceClient(serviceClass : KClass<T>, client : WebClient = createClient()) : T =
		JAXRSClientFactory.fromClient(client, serviceClass.java)