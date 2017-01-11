package com.github.K0zka.kerub.stories.config

import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.login
import com.github.K0zka.kerub.model.ControllerConfig
import com.github.K0zka.kerub.services.ControllerConfigService
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import org.apache.cxf.jaxrs.client.JAXRSClientFactory

class ControllerConfigDefs {

	var originalConfig: ControllerConfig? = null
	var service: ControllerConfigService? = null

	@Before
	fun backup() {
		val client = createClient()
		client.login("admin", "password")
		service = JAXRSClientFactory.fromClient(client, ControllerConfigService::class.java)
		originalConfig = service!!.get()
	}

	@After
	fun restore() {
		service!!.set(originalConfig!!)
	}

	val configs = mapOf<String, (Boolean, ControllerConfig) -> ControllerConfig>(
			"accounts required" to { enabled, config -> config.copy(accountsRequired = enabled) },
			"power management enabled" to { enabled, config -> config.copy(powerManagementEnabled = enabled) },
			"lvm create volume enabled" to { enabled, config -> config.copy(lvmCreateVolumeEnabled = enabled) },
			"gvinum create volume enabled" to { enabled, config -> config.copy(lvmCreateVolumeEnabled = enabled) }
	)

	@Given("Controller configuration '(.*)' is (enabled|disabled)")
	fun setConfigurationBoolean(configName: String, enabled: String) {
		val transform = requireNotNull(configs[configName])
		service!!.set(transform(enabled == "enabled", service!!.get()))
	}

}