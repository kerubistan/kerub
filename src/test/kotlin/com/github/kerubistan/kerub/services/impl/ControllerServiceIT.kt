package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.runRestAction
import com.github.kerubistan.kerub.services.ControllerService
import com.github.kerubistan.kerub.services.LoginService
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