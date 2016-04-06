package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.lom.WakeOnLanInfo
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertTrue

class WakeHostFactoryTest {

	val host1 = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = "test",
			capabilities = HostCapabilities(
					os = null,
					cpuArchitecture = "X86_64",
					distribution = null,
					totalMemory = "2 GB".toSize(),
					powerManagment = listOf(WakeOnLanInfo())
			)
	)

	val host2 = Host(
			address = "host-2.example.com",
			dedicated = true,
			publicKey = "test",
			capabilities = HostCapabilities(
					os = null,
					cpuArchitecture = "X86_64",
					distribution = null,
					totalMemory = "2 GB".toSize(),
					powerManagment = listOf(WakeOnLanInfo())
			)
	)

	val host3 = Host(
			address = "host-3.example.com",
			dedicated = true,
			publicKey = "test",
			capabilities = HostCapabilities(
					os = null,
					cpuArchitecture = "X86_64",
					distribution = null,
					totalMemory = "2 GB".toSize()
			)
	)

	val host4 = Host(
			address = "host-4.example.com",
			dedicated = true,
			publicKey = "test"
	)


	@Test
	fun produceNoLom() {
		assertTrue {
			WakeHostFactory.produce(OperationalState.fromLists(hosts = listOf(host3))).isEmpty()
		}
		assertTrue {
			WakeHostFactory.produce(OperationalState.fromLists(hosts = listOf(host4))).isEmpty()
		}
	}


	@Test
	fun produceSingleHostNorecord() {
		val steps = WakeHostFactory.produce(OperationalState.fromLists(hosts = listOf(host1)))
		Assert.assertEquals(steps.size, 1)
		Assert.assertTrue(steps.all { it is WakeHost && it.host == host1 })
	}

	@Test
	fun produceSingleHostDown() {
		val steps = WakeHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(host1),
				hostDyns = listOf(HostDynamic(id = host1.id, status = HostStatus.Down))
		))
		Assert.assertEquals(steps.size, 1)
		Assert.assertTrue(steps.all { it is WakeHost && it.host == host1 })
	}

	@Test
	fun produceOneUpOneDown() {
		val steps = WakeHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(host1, host2),
				hostDyns = listOf(HostDynamic(id = host2.id, status = HostStatus.Up))
		))
		Assert.assertEquals(steps.size, 1)
		Assert.assertTrue(steps.all { it is WakeHost && it.host == host1 })
	}

}