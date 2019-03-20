package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.hostDown
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import org.junit.Test
import kotlin.test.assertTrue

class OperationalStateTest {

	@Test
	fun getConnectionTargets() {
		assertTrue("key not installed") {
			OperationalState.fromLists(
					hosts = listOf(testHost, testOtherHost),
					hostDyns = listOf(hostUp(testHost), hostUp(testOtherHost))
			).connectionTargets.isEmpty()
		}
		assertTrue("key installed while both server up") {
			val host1 = testHost.copy()
			val host2 = testOtherHost.copy()
			OperationalState.fromLists(
					hosts = listOf(host1, host2),
					hostDyns = listOf(hostUp(host1), hostUp(host2)),
					hostCfgs = listOf(
							HostConfiguration(id = host1.id, publicKey = "HOST-1-PUBKEY"),
							HostConfiguration(id = host2.id, acceptedPublicKeys = listOf("HOST-1-PUBKEY"))
					)
			).connectionTargets == mapOf(host1.id to listOf(host2))
		}
		assertTrue("key installed but server host is down") {
			val host1 = testHost.copy()
			val host2 = testOtherHost.copy()
			OperationalState.fromLists(
					hosts = listOf(host1, host2),
					hostDyns = listOf(hostUp(host1), hostDown(host2)),
					hostCfgs = listOf(
							HostConfiguration(id = host1.id, publicKey = "HOST-1-PUBKEY"),
							HostConfiguration(id = host2.id, acceptedPublicKeys = listOf("HOST-1-PUBKEY"))
					)
			).connectionTargets.isEmpty()
		}

	}

	@Test
	fun recyclingHosts() {
		assertTrue("no hosts, no recycling") {
			OperationalState.fromLists().recyclingHosts.isEmpty()
		}
		assertTrue("no hosts recycling") {
			OperationalState.fromLists(
					hosts = listOf(testHost)
			).recyclingHosts.isEmpty()
		}
		assertTrue("one recycling host") {
			OperationalState.fromLists(
					hosts = listOf(
							testHost,
							testOtherHost.copy(
									recycling = true
							)
					)
			).recyclingHosts.keys == setOf(testOtherHost.id)
		}
	}
}