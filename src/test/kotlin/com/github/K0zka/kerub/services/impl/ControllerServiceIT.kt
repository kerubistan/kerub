package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.services.ControllerService
import com.github.K0zka.kerub.services.LoginService
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

public class ControllerServiceIT {

	@Test
	fun list() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, javaClass<LoginService>(), true)
		login.login(LoginService.UsernamePassword(username = "admin", password = "password"))

		val controllerService = JAXRSClientFactory.fromClient(client, javaClass<ControllerService>(), true)
		val controllers = controllerService.list()

		Assert.assertFalse(controllers.isEmpty())
	}
}