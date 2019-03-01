package com.github.kerubistan.kerub

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.services.exc.mappers.RestError
import com.github.kerubistan.kerub.services.getServiceBaseUrl
import com.github.kerubistan.kerub.utils.createObjectMapper
import com.github.kerubistan.kerub.utils.silent
import com.github.kerubistan.kerub.utils.substringBetween
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper
import org.apache.cxf.jaxrs.client.WebClient
import org.eclipse.jetty.util.HttpCookieStore
import org.eclipse.jetty.websocket.client.WebSocketClient
import java.io.InputStream
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI
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

class RestExceptionHandler(private val objectMapper : ObjectMapper) : ResponseExceptionMapper<Exception> {
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

fun WebClient.logout(): Response =
		this.runRestAction(LoginService::class) {
			it.logout()
			WebClient.client(it).response
		}

fun <X : Any, Y : Any> WebClient.runRestAction(clientClass: KClass<X>, action: (X) -> Y) =
	action(JAXRSClientFactory.fromClient(this, clientClass.java))


fun createSocketClient(resp: Response): WebSocketClient {
	val wsClient = WebSocketClient()
	wsClient.start()
	val cookieStore: CookieStore = HttpCookieStore()
	wsClient.cookieStore = cookieStore
	resp.metadata["Set-Cookie"]?.forEach {
		val cookie = it.toString()
		wsClient.cookieStore.add(
				URI(testWsUrl),
				HttpCookie(cookie.substringBefore("="), cookie.substringBetween("=", ";")))
	}
	return wsClient
}


fun createClient() : WebClient {
	val objectMapper = createObjectMapper()
	val client = WebClient.create(getServiceBaseUrl(), listOf(JacksonJsonProvider(objectMapper), RestExceptionHandler(objectMapper)), true)
	WebClient.getConfig(client).requestContext[org.apache.cxf.message.Message.MAINTAIN_SESSION] = true
	return client
}

fun <T : Any> createServiceClient(serviceClass : KClass<T>, client : WebClient = createClient()) : T =
		JAXRSClientFactory.fromClient(client, serviceClass.java)