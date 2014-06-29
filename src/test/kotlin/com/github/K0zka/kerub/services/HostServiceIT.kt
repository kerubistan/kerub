package com.github.K0zka.kerub.services

import org.junit.Test
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.core.MediaType
import javax.ws.rs.client.Entity
import com.github.K0zka.kerub.model.Host
import java.util.UUID
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider
import org.codehaus.jackson.map.ObjectMapper
import java.io.InputStream
import org.junit.Assert

public class HostServiceIT {

	val client = ClientBuilder.newClient()!!

	{
		client.register(javaClass<JacksonJaxbJsonProvider>())
	}

	Test
	fun add() {
		val host = Host()
		host.id = UUID.randomUUID();
		host.address = "127.0.0.2"
		val objectReader = ObjectMapper().reader(javaClass<Host>())!!
		val putResponse = objectReader.readValue<Host>(
				client.target("${getServiceBaseUrl()}/host")!!
						.request(MediaType.APPLICATION_JSON_TYPE)!!
						.put(Entity.entity(host, MediaType.APPLICATION_JSON_TYPE))!!
						.getEntity() as InputStream)!!

		Assert.assertEquals(host.id, putResponse.id)
		Assert.assertEquals(host.address, putResponse.address)

		val getResponse = objectReader.readValue<Host>(
				client.target("${getServiceBaseUrl()}/host/${putResponse.id}")!!
						.request(MediaType.APPLICATION_JSON_TYPE)!!
						.get()!!
						.getEntity() as InputStream)!!

		Assert.assertEquals(host.id, getResponse.id)
		Assert.assertEquals(host.address, getResponse.address)
	}
}