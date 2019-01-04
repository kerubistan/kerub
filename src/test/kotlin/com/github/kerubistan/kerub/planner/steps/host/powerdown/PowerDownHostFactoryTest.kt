package com.github.kerubistan.kerub.planner.steps.host.powerdown

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.controller.config.ControllerConfig
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.lom.WakeOnLanInfo
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertTrue

class PowerDownHostFactoryTest : AbstractFactoryVerifications(PowerDownHostFactory) {

	val vm = VirtualMachine(
			name = "vm-1",
			nrOfCpus = 1
	)

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	)

	val hostWithPowerManagement = host.copy(
			capabilities = HostCapabilities(
					powerManagment = listOf(WakeOnLanInfo()),
					totalMemory = "16 GB".toSize(),
					cpuArchitecture = "X86_64",
					distribution = null,
					os = null
			)
	)

	@Test
	fun produce() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(hostWithPowerManagement),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up
				))
		))
		Assert.assertEquals(1, steps.size)
		Assert.assertTrue(steps.all { it.host == hostWithPowerManagement })
	}

	@Test
	fun produceWithDisabled() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(hostWithPowerManagement),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up
				)),
				config = ControllerConfig(
						powerManagementEnabled = false
				)
		))
		assertTrue(steps.isEmpty())
	}


	@Test
	fun produceWithNoPowerManagement() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up
				))
		))
		Assert.assertEquals(0, steps.size)
		Assert.assertTrue(steps.all { it.host == host })
	}

	@Test
	fun produceWithHostDown() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(hostWithPowerManagement),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Down
				))
		))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithHostNoRecord() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(hostWithPowerManagement),
				hostDyns = listOf()
		))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithHostNotDedicated() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(host.copy(
						dedicated = false
				)),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up
				))
		))
		Assert.assertTrue(steps.isEmpty())
	}

	@Test
	fun produceWithNoPowerManagementButRecycling() {
		val recycling = host.copy(recycling = true)
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(recycling),
				hostDyns = listOf(HostDynamic(
						id = recycling.id,
						status = HostStatus.Up
				))
		))
		Assert.assertEquals(listOf(PowerDownHost(recycling)), steps)
		Assert.assertTrue(steps.all { it.host == recycling })

	}

}