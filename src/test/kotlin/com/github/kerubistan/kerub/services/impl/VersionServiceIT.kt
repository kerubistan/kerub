package com.github.kerubistan.kerub.services.impl

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.github.kerubistan.kerub.services.VersionService
import com.github.kerubistan.kerub.services.getServiceBaseUrl
import com.github.kerubistan.kerub.utils.getLogger
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Assert
import org.junit.Test

class VersionServiceIT {

	private companion object

	val logger = getLogger()

	@Test
	fun getVersionInfo() {
		val service = JAXRSClientFactory.create(
				getServiceBaseUrl(),
				VersionService::class.java,
				listOf(JacksonJsonProvider()))!!
		val versionInfo = service.getVersionInfo()
		Assert.assertNotNull(versionInfo)
		logger.info("version: ${versionInfo.version}")
		logger.info("title: ${versionInfo.title}")
		logger.info("vendor: ${versionInfo.vendor}")
	}
}