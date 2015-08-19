package com.github.K0zka.kerub

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.K0zka.kerub.security.mappers.RestError
import com.github.K0zka.kerub.services.getServiceBaseUrl
import com.github.K0zka.kerub.utils.createObjectMapper
import org.apache.cxf.jaxrs.client.ResponseExceptionMapper
import org.apache.cxf.jaxrs.client.WebClient
import javax.ws.rs.core.Response

class RestException(val msg : String, val code : String, val status : Int) : Exception(msg)

class RestExceptionHandler : ResponseExceptionMapper<Exception> {
	override fun fromResponse(r: Response?): Exception? {
		val entity = r!!.getEntity() as RestError
		return RestException(entity.message!!,entity.code!!,r!!.getStatus())
	}
}

fun createClient() : WebClient {
	val client = WebClient.create(getServiceBaseUrl(), listOf(JacksonJsonProvider(createObjectMapper())), true)
	WebClient.getConfig(client).getRequestContext().put(
			org.apache.cxf.message.Message.MAINTAIN_SESSION, true)
	return client;
}