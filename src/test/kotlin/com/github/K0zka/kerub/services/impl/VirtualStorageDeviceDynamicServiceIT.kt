package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.services.VirtualNetworkService
import com.github.K0zka.kerub.services.VirtualStorageDeviceDynamicService
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Test
import java.util.UUID

class VirtualStorageDeviceDynamicServiceIT {
	@Test
	fun security() {

		val anonService = JAXRSClientFactory.fromClient(createClient(),
				VirtualStorageDeviceDynamicService::class.java, true)

		expect(RestException::class,
				action = { anonService.getById(UUID.randomUUID()) },
				check = { it.code == "AUTH1" })
	}
}