package com.github.kerubistan.kerub.stories.rest.auth

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.services.HostJoinDetails
import com.github.kerubistan.kerub.services.HostService
import com.github.kerubistan.kerub.services.LoginService
import com.github.kerubistan.kerub.services.MotdService
import com.github.kerubistan.kerub.services.VersionService
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.utils.toSize
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.apache.cxf.jaxrs.client.WebClient
import org.hamcrest.CoreMatchers
import org.junit.Assert
import java.math.BigInteger
import javax.ws.rs.core.Response
import kotlin.reflect.KClass
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthenticationDefinitions {
	var user = "anonymous"
	var password = "not set"

	var client = createClient()
	var exception: RestException? = null
	var response: Response? = null
	var service: Any? = null

	@Before
	fun reset() {
		client.reset()
	}

	@Given("^user (\\S+) with password (\\S+)$")
	fun setUserAndPassword(user: String, password: String) {
		this.user = user
		this.password = password
	}

	@Given("^anonymous user$")
	fun setAnonUser() {
		//do nothing
	}

	@When("^user tries to retrieve host list$")
	fun tryRetrieveHostList() {
		tryRunRestAction(HostService::class, {
			it.listAll(start = 0, limit = 100, sort = "address")
		})
	}

	@Then("^request must be rejected$")
	fun verifyRequestRejected() {
		Assert.assertThat("request must be rejected", exception, CoreMatchers.notNullValue())
	}

	@Then("^the response code must be (\\d+)$")
	fun verifyResponseCode(expectedErrorCode: Int) {
		Assert.assertThat("the response code must be ${expectedErrorCode}",
		                  exception?.status,
		                  CoreMatchers.equalTo(expectedErrorCode))
	}

	@Then("^the error code must be (\\S+)$")
	fun verifyErrorCode(expectedErrorCode: String) {
		Assert.assertThat("the error code must be ${expectedErrorCode}",
		                  exception?.code,
		                  CoreMatchers.equalTo(expectedErrorCode))
	}

	@Then("^no session should be created$")
	fun verifyNoSession() {
		if(exception != null) {
			Assert.assertThat(
					"no session should be created",
					exception?.response?.cookies?.keys?.contains("JSESSIONID"),
					CoreMatchers.equalTo(false)
			                 )
		} else {
			assertFalse { WebClient.client(service).response.cookies.containsKey("JSESSIONID")  }
		}
	}

	@When("^user tries to retrieve vm list$")
	fun tryRetrieveVmList() {
		tryRunRestAction( VirtualMachineService::class, {
			it.listAll(start = 0, limit = 100, sort = "id")
		})
	}

	@When("^user tries to create new vm$")
	fun tryCreateNewVm() {
		tryRunRestAction(VirtualMachineService::class, {
			it.add(VirtualMachine(
				name = "test",
			    expectations = listOf(),
			    nrOfCpus = 2,
			    memory = Range<BigInteger>(min = "1 GB".toSize(), max = "2 GB".toSize())
			                     ))
		})
	}

	@When("^user tries to get host public key$")
	fun tryGetHostPublicKey() {
		tryRunRestAction(HostService::class, {
			it.getHostPubkey("example.com")
		})
	}

	@When("^user tries to log in$")
	fun tryLogin() {
		tryRunRestAction(LoginService::class, {
			it.login(LoginService.UsernamePassword(username = user, password = password))
		})
	}

	@When("^user tries to get motd$")
	fun tryGetMotd() {
		tryRunRestAction(MotdService::class, { it.get() })
	}

	@When("^user tries to get version$")
	fun tryGetVersion() {
		tryRunRestAction(VersionService::class, { it.getVersionInfo() })
	}

	fun <X : Any> tryRunRestAction(clientClass: KClass<X>, action: (X) -> Unit) {
		try {
			val serviceClient = JAXRSClientFactory.fromClient(client, clientClass.java)
			service = serviceClient
			action(serviceClient)
		} catch (re: RestException) {
			exception = re
		}
	}

	@Then("^request must pass$")
	fun verifyRequestPassed() {
		Assert.assertThat("request must pass", exception, CoreMatchers.nullValue())
	}

	@When("^user tries to join new host$")
	fun tryJoinNewHost() {
		tryRunRestAction(HostService::class, {
			it.joinWithoutPassword(HostJoinDetails(host = testHost))
		})
	}

	@Then("^session should be created$")
	fun verifySessionCreated() {
		assertTrue { WebClient.client(service).response.cookies.containsKey("JSESSIONID")  }
	}

}