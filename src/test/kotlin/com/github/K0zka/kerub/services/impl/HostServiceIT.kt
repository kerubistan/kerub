package com.github.K0zka.kerub.services.impl

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.services.HostService
import com.github.K0zka.kerub.services.LoginService
import com.github.K0zka.kerub.services.getServiceBaseUrl
import com.github.K0zka.kerub.utils.createObjectMapper
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.WebClient
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

public class HostServiceIT {

	//TODO: make a cucumber story out of this
	@Test
	fun getPubkey() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, javaClass<LoginService>(), true)
		login.login(LoginService.UsernamePassword(username = "admin", password = "password"))
		val service = JAXRSClientFactory.fromClient(client,
				javaClass<HostService>(), true)
		Assert.assertThat(service.getPubkey(), CoreMatchers.notNullValue())
	}

}