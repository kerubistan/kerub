package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.security.Roles
import com.github.K0zka.kerub.services.LoginService
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class LoginServiceIT {

	@Test
	fun login() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, LoginService::class.java, true)
		login.login(LoginService.UsernamePassword(username = "admin", password = "password"))
		val user = login.getUser()

		assertEquals("admin", user.name)
		assertEquals(listOf(Roles.Admin), user.roles)
	}

	@Test
	fun getUserWithoutLogin() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, LoginService::class.java, true)

		try {
			login.getUser()
			fail("exception expected")
		} catch (re: RestException) {
			assertEquals("AUTH1", re.code)
			assertEquals("Authentication needed", re.msg)
			assertEquals(401, re.status)
		}
	}

	@Test
	fun getUserAfterLogout() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, LoginService::class.java, true)
		login.login(LoginService.UsernamePassword(username = "admin", password = "password"))
		login.logout()

		expect(RestException::class) {
			login.getUser()
		}
	}

}