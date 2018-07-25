package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.services.ControllerService
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.testUsers
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Assert
import org.junit.Test

class ControllerServiceSecurityIT {

	fun check(role: String, access: Boolean) {
		val user = testUsers[role]!!
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, LoginService::class.java, true)
		login.login(LoginService.UsernamePassword(user.first, user.second))
		val controller = JAXRSClientFactory.fromClient(client, ControllerService::class.java, true)
		try {
			controller.list()
			if (!access) {
				Assert.fail("User ${user.first} in role $role should not have access")
			}
		} catch (e: RestException) {
			if (access) {
				Assert.fail("User ${user.first} in role $role should have access")
			}
		}
	}

	@Test
	fun checkAdmin() {
		check("admin", true)
	}

	@Test
	fun checkUser() {
		check("user", false)
	}

}