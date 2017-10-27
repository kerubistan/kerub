package com.github.kerubistan.kerub.stories.config

import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.services.ControllerConfigService
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import org.apache.cxf.jaxrs.client.JAXRSClientFactory

class ControllerConfigDefs {

	var originalConfig: ControllerConfig? = null
	var service: ControllerConfigService? = null

	companion object {
		val configs = mapOf<String, (Boolean, ControllerConfig) -> ControllerConfig>(
				"accounts required" to { enabled, config -> config.copy(accountsRequired = enabled) },
				"power management enabled" to { enabled, config -> config.copy(powerManagementEnabled = enabled) },
				"wake on lan enabled" to { enabled, config -> config.copy(wakeOnLanEnabled = enabled) },
				"lvm create volume enabled" to { enabled, config -> config.copy(
						storageTechnologies = config.storageTechnologies.copy(lvmCreateVolumeEnabled = enabled)
				) },
				"gvinum create volume enabled" to { enabled, config -> config.copy(
						storageTechnologies = config.storageTechnologies.copy(gvinumCreateVolumeEnabled = enabled)
				) }
		)
	}

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

	@Given("Controller configuration '(.*)' is (enabled|disabled)")
	fun setConfigurationBoolean(configName: String, enabled: String) {
		val transform = requireNotNull(configs[configName])
		service!!.set(transform(enabled == "enabled", service!!.get()))
	}

}