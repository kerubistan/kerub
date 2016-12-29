package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.runRestAction
import com.github.K0zka.kerub.services.ControllerService
import com.github.K0zka.kerub.services.LoginService
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals

class ControllerServiceIT {

	@Test
	fun security() {
		createClient().runRestAction(ControllerService::class) {
			expect(RestException::class,
					action = { it.list() },
					check = { assertEquals("AUTH1", it.code) })
		}
		val endUserClient = createClient()
		endUserClient.login("enduser", "password")
		endUserClient.runRestAction(ControllerService::class) {
			expect(RestException::class,
					action = { it.list() },
					check = { assertEquals("SEC1", it.code) })
		}
	}

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