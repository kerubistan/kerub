package com.github.kerubistan.kerub.stories.rest.errorcodes

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.services.HostAndPassword
import com.github.kerubistan.kerub.services.HostJoinDetails
import com.github.kerubistan.kerub.services.HostService
import com.github.kerubistan.kerub.services.LoginService
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.WebClient
import org.hamcrest.CoreMatchers
import org.junit.Assert

class RestErrorCodesDefinitions {

	var hostAddress: String? = null
	var publicKeyFingerPrint = ""
	var client: WebClient? = null
	var hostService: HostService? = null
	var exception: RestException? = null

	@Before
	fun setup() {
		client = createClient()
		hostService = JAXRSClientFactory.fromClient(client, HostService::class.java)
	}

	@Given("^user (\\S+) with password (\\S+)$")
	fun login(userName: String, password: String) {
		JAXRSClientFactory.fromClient(client, LoginService::class.java).login(LoginService.UsernamePassword(
				username = userName,
				password = password
		))
	}

	@Given("^a host address (\\S+)$")
	fun setHostAddr(hostAddress: String) {
		this.hostAddress = hostAddress
	}

	@Given("^public key fingerprint (\\S+)$")
	fun setPublicKey(pubKeyFingerPrint: String) {
		this.publicKeyFingerPrint = pubKeyFingerPrint
	}

	@When("^the client tries to join the server with password (\\S+)$")
	fun tryJoinServerWithPassword(hostPassword: String) {
		try {
			hostService!!.join(HostAndPassword(
					host = Host(
							address = hostAddress!!,
							publicKey = publicKeyFingerPrint,
							dedicated = false
					),
					password = hostPassword
			))
		} catch (webExc: RestException) {
			this.exception = webExc
		}
	}

	@When("^the client tries to join the server$")
	fun tryJoinServer() {
		try {
			hostService!!.joinWithoutPassword(HostJoinDetails(host = Host(
					address = hostAddress!!,
					publicKey = publicKeyFingerPrint,
					dedicated = false
			)))
		} catch (webExc: RestException) {
			this.exception = webExc
		}
	}

	//TODO: duplicate with verifyErrorCode?
	@Then("^the response code must be (\\d+)$")
	fun verifyResponseCode(expectedResponseCode: Int) {
		Assert.assertThat(exception?.status, CoreMatchers.equalTo(expectedResponseCode))
	}

	@Then("^the content type must be (\\S+)$")
	fun verifyContentType(expectedContentType: String) {
		Assert.assertThat(exception?.response?.getHeaderString("Content-Type"), CoreMatchers.equalTo(expectedContentType))
	}

	@Then("^the error code must be (\\S+)$")
	fun verifyErrorCode(expectedErrorCode: String) {
		Assert.assertThat(exception?.code, CoreMatchers.equalTo(expectedErrorCode))
	}

	@When("^the client tries to retrieve public key$")
	fun retrievePublicKey() {
		try {
			hostService!!.joinWithoutPassword(HostJoinDetails(Host(
					address = hostAddress!!,
					publicKey = publicKeyFingerPrint,
					dedicated = false
			)))
		} catch (webExc: RestException) {
			this.exception = webExc
		}
	}
}