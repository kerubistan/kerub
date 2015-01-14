package com.github.K0zka.kerub.services.impl

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.junit.Test
import javax.ws.rs.client.ClientBuilder
import java.util.UUID
import com.github.K0zka.kerub.model.Host
import org.apache.cxf.jaxrs.client.JAXRSClientFactory

import com.github.K0zka.kerub.services.getServiceBaseUrl
import com.github.K0zka.kerub.services.HostService
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.K0zka.kerub.utils.createObjectMapper

public class HostServiceIT {

	Test
	fun add() {
		val host = Host(
				id = UUID.randomUUID(),
				address = "127.0.0.2",
				publicKey = "",
				dedicated = true
		               )

		val service = JAXRSClientFactory.create(
				getServiceBaseUrl(),
				javaClass<HostService>(),
				listOf(JacksonJsonProvider(createObjectMapper())))!!

		service.add(host)
	}
}