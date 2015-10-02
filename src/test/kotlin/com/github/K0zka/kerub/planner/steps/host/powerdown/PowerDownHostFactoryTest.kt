package com.github.K0zka.kerub.planner.steps.host.powerdown

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Assert
import org.junit.Test

public class PowerDownHostFactoryTest {

	val vm = VirtualMachine(
			name = "vm-1",
	        nrOfCpus = 1
	                       )

	val host = Host(
			address = "host-1.example.com",
			dedicated = true,
			publicKey = ""
	               )

	@Test
	fun produce() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						status = HostStatus.Up
				                             ))
		                                                                   ))
		Assert.assertEquals(1, steps.size())
		Assert.assertTrue(steps.all { it.host == host })
	}

	@Test
	fun produceWithHostDown() {
		val steps = PowerDownHostFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
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
				hosts = listOf(host),
				hostDyns = listOf()
		                                                                   ))
		Assert.assertTrue(steps.isEmpty())
	}

}