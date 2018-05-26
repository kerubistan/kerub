package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.RestException
import com.github.kerubistan.kerub.createClient
import com.github.kerubistan.kerub.expect
import com.github.kerubistan.kerub.login
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualNetwork
import com.github.kerubistan.kerub.model.devices.NetworkAdapterType
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.services.VirtualNetworkService
import com.github.kerubistan.kerub.testVirtualNetwork
import org.apache.cxf.jaxrs.client.JAXRSClientFactory.fromClient
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class VirtualNetworkServiceIT {
	@Test
	fun security() {
		val anonService = fromClient(createClient(),
				VirtualNetworkService::class.java, true)

		expect(
				clazz = RestException::class,
				action = { anonService.delete(randomUUID()) },
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
				action = { anonService.getById(randomUUID()) },
				check = { assertEquals(it.code, "AUTH1") }
		)

		expect(
				clazz = RestException::class,
				action = { anonService.update(randomUUID(), testVirtualNetwork) },
				check = { assertEquals(it.code, "AUTH1") }
		)

	}

	@Test
	fun consistency() {
		val client = createClient()
		client.login()

		val vmService = fromClient(client,
				VirtualMachineService::class.java, true)

		fromClient(client,
				VirtualNetworkService::class.java, true).apply {
			val network = add(
					VirtualNetwork(
							id = randomUUID(),
							name = "test virtual network"
					)
			)

			val unusedNetwork = add(
					VirtualNetwork(
							id = randomUUID(),
							name = "unused test virtual network"
					)
			)

			val vm = vmService.add(
					entity = VirtualMachine(
							id = randomUUID(),
							name = "test vm for network ${network.id}",
							devices = listOf(
									NetworkDevice(
											adapterType = NetworkAdapterType.rtl8139,
											networkId = network.id
									)
							)
					)
			)

			delete(unusedNetwork.id)

			expect(RestException::class) {
				delete(network.id)
			}

			vmService.delete(vm.id)

			delete(network.id)
		}
	}
}