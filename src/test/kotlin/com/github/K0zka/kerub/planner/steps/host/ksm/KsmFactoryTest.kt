package com.github.K0zka.kerub.planner.steps.host.ksm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Assert
import org.junit.Test

class KsmFactoryTest {

	val host = Host(
			address = "host-1.example.com",
			publicKey = "",
			dedicated = true
	               )

	@Test
	fun produceWithoutHosts() {
		Assert.assertTrue(KsmFactory.produce(OperationalState.fromLists()).isEmpty())
	}

	@Test
	fun produceWithHostDown() {
		Assert.assertTrue(KsmFactory.produce(OperationalState.fromLists(
				hosts = listOf(host)
		                                                               )).isEmpty())
	}

	@Test
	fun produceWithHostAlreadyEnabled() {
		val list = KsmFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						ksmEnabled = true
				                             ))
		                                                        ))
		Assert.assertEquals(list.size, 1)
		Assert.assertTrue( list[0] is DisableKsm )
	}

	@Test
	fun produceWithHostAlreadyDisabled() {
		val list = KsmFactory.produce(OperationalState.fromLists(
				hosts = listOf(host),
				hostDyns = listOf(HostDynamic(
						id = host.id,
						ksmEnabled = false
				                             ))
		                                                        ))
		Assert.assertEquals(list.size, 1)
		Assert.assertTrue( list[0] is EnableKsm )
	}

}