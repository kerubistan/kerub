package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.services.ControllerService
import com.github.K0zka.kerub.services.LoginService
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Assert
import org.junit.Test

class ControllerServiceIT {

	@Test
	fun list() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, LoginService::class.java, true)
		login.login(LoginService.UsernamePassword(username = "admin", password = "password"))

		val controllerService = JAXRSClientFactory.fromClient(client, ControllerService::class.java, true)
		val controllers = controllerService.list()

		Assert.assertFalse(controllers.isEmpty())
	}
}