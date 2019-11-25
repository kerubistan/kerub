package com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RemoveOvsPortExecutorTest {
	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostConfigurationDao = mock<HostConfigurationDao>()
		val session = mock<ClientSession>()
		hostCommandExecutor.mockHost(testHost, session)
		session.mockCommandExecution("ovs-vsctl.*".toRegex())

		var updatedConfig: HostConfiguration? = null
		doAnswer {
			val hostId = it.arguments[0] as UUID
			val change = it.arguments[2] as (HostConfiguration) -> HostConfiguration
			updatedConfig = change(
					HostConfiguration(
							id = hostId,
							networkConfiguration = listOf(
									OvsNetworkConfiguration(
											virtualNetworkId = testVirtualNetwork.id,
											ports = listOf(
													OvsDataPort(
															name = testVm.idStr
													),
													OvsDataPort(
															name = randomUUID().toString()
													)
											)
									)
							)
					)
			)
		}.whenever(hostConfigurationDao).update(eq(testHost.id), any(), any())

		RemoveOvsPortExecutor(hostCommandExecutor, hostConfigurationDao).execute(
				RemoveOvsPort(
						host = testHost,
						virtualNetwork = testVirtualNetwork,
						portName = testVm.idStr
				)
		)

		assertEquals(1, updatedConfig!!.networkConfiguration.size)
		assertTrue { (updatedConfig!!.networkConfiguration.single() as OvsNetworkConfiguration).ports.none { it.name == testVm.idStr } }
		assertEquals(1, (updatedConfig!!.networkConfiguration.single() as OvsNetworkConfiguration).ports.size)
	}
}