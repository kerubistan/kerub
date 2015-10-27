package com.github.K0zka.kerub.planner.steps.host.startup

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Assert
import org.junit.Test

public class WakeHostFactoryTest {

	val host1 = Host(
			address = "host-1.example.com",
	        dedicated = true,
	        publicKey = "test"
	                )

	val host2 = Host(
			address = "host-2.example.com",
			dedicated = true,
			publicKey = "test"
	                )

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