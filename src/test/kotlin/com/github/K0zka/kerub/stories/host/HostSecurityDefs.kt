package com.github.K0zka.kerub.stories.host

import com.github.K0zka.kerub.utils.getLogger
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.Assert

public class HostSecurityDefs {
	var user: String = "admin"
	var responseCode = 200
	val baseUrl = "http://localhost:${System.getProperty("kerub.it.port") ?: "8080"}/"

	companion object private val logger = getLogger(HostSecurityDefs::class)

	Given("A controller")
	fun givenAController() {
	}

	Given("(\\S+) user")
	fun givenAUser(user: String) {
		this.user = user
	}

	When("user tries to (\\S+) hosts")
	fun userAction(action: String) {
		val client = HttpClientBuilder.create().build()

		val login = HttpPost("$baseUrl/s/r/auth/login")
		login.setEntity(StringEntity("""
			{"username":"$user","password":"password"}
		""", ContentType.APPLICATION_JSON))
		val loginResp = client.execute(login)
		logger.info("login: {}",loginResp.getStatusLine().getStatusCode())


		when (action) {
			"list" -> {
				responseCode = client.execute(HttpGet("$baseUrl/s/r/host")).getStatusLine().getStatusCode()
			}
			"join" -> {
				val join = HttpPut("$baseUrl/s/r/host/join")
				join.setEntity(StringEntity("""
				{"host":
					{
						"@type":"host",
						"id":"7880f78c-e21b-4452-ac52-8d8e9d74c463",
						"address":"192.168.122.221",
						"publicKey":"43:99:3d:4f:35:8f:29:21:28:a2:ba:35:76:75:f0:af",
						"dedicated":true
					},
				"password":""}
				""", ContentType.APPLICATION_JSON))
				responseCode = client.execute(join).getStatusLine().getStatusCode()
			}
			"remove" -> {
				responseCode = client.execute(HttpDelete("$baseUrl/s/r/host/7880f78c-e21b-4452-ac52-8d8e9d74c463"))
						.getStatusLine().getStatusCode()
			}
			"update" -> {
				val update = HttpPost("$baseUrl/s/r/host/7880f78c-e21b-4452-ac52-8d8e9d74c463")
				update.setEntity(StringEntity(""" {"@type":"host","id":"7880f78c-e21b-4452-ac52-8d8e9d74c463","address":"192.168.122.221","publicKey"
:"43:99:3d:4f:35:8f:29:21:28:a2:ba:35:76:75:f0:af","dedicated":true}""", ContentType.APPLICATION_JSON))
				responseCode = client.execute(update).getStatusLine().getStatusCode()

			}
			else -> {
				throw IllegalArgumentException("not handled action")
			}
		}
	}

	Then("(\\S+) should be received")
	fun assertResponseCode(responseCode: Int) {
		Assert.assertEquals(responseCode, this.responseCode)
	}
}