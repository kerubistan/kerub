package com.github.K0zka.kerub.services.impl

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.K0zka.kerub.services.MotdService
import com.github.K0zka.kerub.services.VersionService
import com.github.K0zka.kerub.services.getServiceBaseUrl
import com.github.K0zka.kerub.utils.getLogger
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

public class MotdServiceIT {

	companion object {
		val logger = getLogger(MotdServiceIT::class)
	}

	@Test
	fun get() {
		val service = JAXRSClientFactory.create(
				getServiceBaseUrl(),
				MotdService::class.java,
				listOf(JacksonJsonProvider()))!!
		val motd = service.get()
		Assert.assertThat(motd, CoreMatchers.notNullValue())
		logger.info("motd: {}", motd)
	}

}