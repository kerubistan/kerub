package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.services.VirtualStorageDeviceDynamicService
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