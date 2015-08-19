package com.github.K0zka.kerub.stories.rest

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.services.HostAndPassword
import com.github.K0zka.kerub.services.HostService
import com.github.K0zka.kerub.services.LoginService
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.WebClient
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.hamcrest.CoreMatchers
import org.junit.Assert
import javax.ws.rs.WebApplicationException

public class RestDefinitions {

	var hostAddress : String? = null
	var publicKeyFingerPrint = ""
	var client : WebClient? = null
	var hostService : HostService? = null
	var exception : WebApplicationException? = null

	Before()
	fun setup() {
		client = createClient()
		hostService = JAXRSClientFactory.fromClient(client, javaClass<HostService>() )
	}

	Given("^user (\\S+) with password (\\S+)$")
	fun login(userName : String, password: String) {
		JAXRSClientFactory.fromClient(client, javaClass<LoginService>() ).login(LoginService.UsernamePassword(
				username = userName,
		        password = password
		                                                                                                     ))
	}

	Given("^a host address (\\S+)$")
	fun setHostAddr(hostAddress:String) {
		this.hostAddress = hostAddress
	}

	Given("^public key fingerprint (\\S+)$")
	fun setPublicKey(pubKeyFingerPrint:String) {
		this.publicKeyFingerPrint = pubKeyFingerPrint
	}

	When("^the client tries to join the server with password (\\S+)$")
	fun tryJoinServerWithPassword(hostPassword : String) {
		try {
			hostService!!.join(HostAndPassword(
					host = Host(
							address = hostAddress!!,
							publicKey = publicKeyFingerPrint,
							dedicated = false
					           ),
			        password = hostPassword
			                                  ) )
		} catch (webExc : WebApplicationException) {
			this.exception = webExc
		}
	}

	When("^the client tries to join the server$")
	fun tryJoinServer() {
		try {
			hostService!!.joinWithoutPassword(Host(
					address = hostAddress!!,
					publicKey = publicKeyFingerPrint,
					dedicated = false
			                                      ))
		} catch (webExc : WebApplicationException) {
			this.exception = webExc
		}
	}

	Then("^the response code must be (\\d+)$")
	fun the_response_code_must_be(expectedResponseCode : Int) {
		Assert.assertThat(exception?.getResponse()?.getStatus(), CoreMatchers.equalTo(expectedResponseCode))
	}

	Then("^the content type must be (\\S+)$")
	fun the_content_type_must_be_application_json(expectedContentType : String) {
		Assert.assertThat(exception?.getResponse()?.getHeaderString("Content-Type"), CoreMatchers.equalTo(expectedContentType))
	}

	Then("^the error code must be (\\S+)$")
	fun the_error_code_must_be(expectedErrorCode : String) {
	}

	When("^the client tries to retrieve public key$")
	fun retrievePublicKey() {
		try {
			hostService!!.joinWithoutPassword(Host(
					address = hostAddress!!,
					publicKey = publicKeyFingerPrint,
					dedicated = false
			                                      ))
		} catch (webExc : WebApplicationException) {
			this.exception = webExc
		}

	}
}