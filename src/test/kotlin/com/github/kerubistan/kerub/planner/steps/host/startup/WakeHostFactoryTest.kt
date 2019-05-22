package com.github.kerubistan.kerub.planner.steps.host.startup

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import io.github.kerubistan.kroki.size.GB
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertTrue

class WakeHostFactoryTest : AbstractFactoryVerifications(WakeHostFactory) {

	val host1 = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = "test",
			capabilities = HostCapabilities(
					os = null,
					cpuArchitecture = "X86_64",
					distribution = null,
					totalMemory = 2.GB,
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
					totalMemory = 2.GB,
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
					totalMemory = 2.GB
			)
	)

	val host4 = Host(
			address = "host-4.example.com",
			dedicated = true,
			publicKey = "test"
	)

	@Test
	fun produceDisabled() {
		assertTrue(
				WakeHostFactory.produce(
						OperationalState.fromLists(
								hosts = listOf(host1, host2),
								hostDyns = listOf(HostDynamic(id = host2.id, status = HostStatus.Up)),
								config = ControllerConfig(powerManagementEnabled = false)
						)).isEmpty())
	}

	@Test
	fun produceNoLom() {
		assertTrue {
			WakeHostFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host3),
							config = ControllerConfig(powerManagementEnabled = true, wakeOnLanEnabled = true)))
					.isEmpty()
		}
		assertTrue {
			WakeHostFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host4),
							config = ControllerConfig(powerManagementEnabled = true, wakeOnLanEnabled = true)
					)).isEmpty()
		}
	}


	@Test
	fun produceSingleHostNorecord() {
		val steps = WakeHostFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(host1),
						config = ControllerConfig(powerManagementEnabled = true, wakeOnLanEnabled = true)
				))
		Assert.assertEquals(1, steps.size)
		Assert.assertTrue(steps.all { it.host == host1 })
	}

	@Test
	fun produceSingleHostDown() {
		val steps = WakeHostFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(host1),
						hostDyns = listOf(HostDynamic(id = host1.id, status = HostStatus.Down)),
						config = ControllerConfig(powerManagementEnabled = true, wakeOnLanEnabled = true)
				))
		Assert.assertEquals(steps.size, 1)
		Assert.assertTrue(steps.all { it.host == host1 })
	}

	@Test
	fun produceOneUpOneDown() {
		val steps = WakeHostFactory.produce(
				OperationalState.fromLists(
						hosts = listOf(host1, host2),
						hostDyns = listOf(HostDynamic(id = host2.id, status = HostStatus.Up)),
						config = ControllerConfig(powerManagementEnabled = true, wakeOnLanEnabled = true)
				))
		Assert.assertEquals(steps.size, 1)
		Assert.assertTrue(steps.all { it.host == host1 })
	}

}