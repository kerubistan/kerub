package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.services.VirtualStorageDeviceDynamicService
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class VirtualStorageDeviceDynamicServiceIT {
	@Test
	fun security() {

		val anonService = JAXRSClientFactory.fromClient(createClient(),
				VirtualStorageDeviceDynamicService::class.java, true)

		expect(RestException::class,
				action = { anonService.getById(UUID.randomUUID()) },
				check = { assertEquals("AUTH1", it.code) })
	}
}