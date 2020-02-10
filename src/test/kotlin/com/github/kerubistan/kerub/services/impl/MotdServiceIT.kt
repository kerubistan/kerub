package com.github.kerubistan.kerub.services.impl

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.kerubistan.kerub.services.MotdService
import com.github.kerubistan.kerub.services.getServiceBaseUrl
import com.github.kerubistan.kerub.utils.getLogger
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class MotdServiceIT {

	companion object {
		private val logger = getLogger()
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