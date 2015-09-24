package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.services.ControllerService
import com.github.K0zka.kerub.services.LoginService
import com.github.K0zka.kerub.testUsers
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Assert
import org.junit.Test

public class ControllerServiceSecurityIT {

	fun check(role: String, access: Boolean) {
		val user = testUsers[role]!!
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, javaClass<LoginService>(), true)
		login.login(LoginService.UsernamePassword(user.first, user.second))
		val controller = JAXRSClientFactory.fromClient(client, javaClass<ControllerService>(), true)
		try {
			controller.list()
			if (!access) {
				Assert.fail("User ${user.first} in role ${role} should not have access")
			}
		} catch (e: RestException) {
			if (access) {
				Assert.fail("User ${user.first} in role ${role} should have access")
			}
		}
	}

	@Test
	fun checkAdmin() {
		check("admin", true)
	}

	@Test
	fun checkPowerUser() {
		check("poweruser", false)
	}

	@Test
	fun checkUser() {
		check("user", false)
	}

}