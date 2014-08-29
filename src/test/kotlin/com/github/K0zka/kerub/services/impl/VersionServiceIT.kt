package com.github.K0zka.kerub.services.impl

import org.junit.Test
import org.junit.Assert
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import com.github.K0zka.kerub.services.getServiceBaseUrl
import com.github.K0zka.kerub.services.VersionService
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.slf4j.LoggerFactory

public class VersionServiceIT {

	class object val logger = LoggerFactory.getLogger(javaClass<VersionServiceIT>())!!

	Test
	fun getVersionInfo() {
		val service = JAXRSClientFactory.create(
				getServiceBaseUrl(),
				javaClass<VersionService>(),
				listOf(JacksonJaxbJsonProvider()))!!
		val versionInfo = service.getVersionInfo()
		Assert.assertNotNull(versionInfo)
		logger.info("version: ${versionInfo.version}")
		logger.info("title: ${versionInfo.title}")
		logger.info("vendor: ${versionInfo.vendor}")
	}
}