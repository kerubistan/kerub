package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.RestException
import com.github.K0zka.kerub.createClient
import com.github.K0zka.kerub.expect
import com.github.K0zka.kerub.model.VirtualNetwork
import com.github.K0zka.kerub.services.VirtualNetworkService
import com.github.K0zka.kerub.testVirtualNetwork
import org.apache.cxf.jaxrs.client.JAXRSClientFactory
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class VirtualNetworkServiceIT {
	@Test
	fun security() {
		val anonService = JAXRSClientFactory.fromClient(createClient(),
				VirtualNetworkService::class.java, true)

		expect(
				clazz = RestException::class,
				action = { anonService.delete(UUID.randomUUID()) },
				check = { assertEquals(it.code, "AUTH1") }
		)

		expect(
				clazz = RestException::class,
				action = { anonService.add(testVirtualNetwork) },
				check = { assertEquals(it.code, "AUTH1") }
		)

		expect(
				clazz = RestException::class,
				action = { anonService.listAll(start = 0, limit = 100, sort = VirtualNetwork::name.name) },
				check = { assertEquals(it.code, "AUTH1") }
		)

		expect(
				clazz = RestException::class,
				action = { anonService.listAll(start = 0, limit = 100, sort = VirtualNetwork::name.name) },
				check = { assertEquals(it.code, "AUTH1") }
		)

		expect(
				clazz = RestException::class,
				action = { anonService.getById(UUID.randomUUID()) },
				check = { assertEquals(it.code, "AUTH1") }
		)

		expect(
				clazz = RestException::class,
				action = { anonService.update(UUID.randomUUID(), testVirtualNetwork) },
				check = { assertEquals(it.code, "AUTH1") }
		)

	}
}