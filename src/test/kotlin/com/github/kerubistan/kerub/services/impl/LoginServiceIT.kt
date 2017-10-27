package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.security.Roles
import com.github.kerubistan.kerub.services.LoginService
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
	fun loginWithUser() {
		val client = createClient()
		val login = JAXRSClientFactory.fromClient(client, LoginService::class.java, true)
		login.login(LoginService.UsernamePassword(username = "user", password = "password"))
		val user = login.getUser()

		assertEquals("user", user.name)
		assertEquals(listOf(Roles.User), user.roles)
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