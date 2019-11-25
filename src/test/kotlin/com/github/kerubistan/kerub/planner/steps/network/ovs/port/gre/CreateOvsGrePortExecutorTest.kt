package com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsGrePort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testOtherHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

internal class CreateOvsGrePortExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		session.mockCommandExecution("ovs-vsctl add-port.*".toRegex())
		hostCommandExecutor.mockHost(testHost, session)
		val hostConfigurationDao = mock<HostConfigurationDao>()
		var updatedHostConfiguration: HostConfiguration? = null
		doAnswer {
			val hostId = it.arguments[0] as UUID
			val change = it.arguments[2] as (HostConfiguration) -> HostConfiguration
			updatedHostConfiguration = change(
					HostConfiguration(
							id = hostId,
							networkConfiguration = listOf(
									OvsNetworkConfiguration(
											virtualNetworkId = testVirtualNetwork.id
									)
							)
					)
			)
		}.whenever(hostConfigurationDao).update(eq(testHost.id), any(), any())
		CreateOvsGrePortExecutor(hostCommandExecutor, hostConfigurationDao).execute(
				CreateOvsGrePort(
						firstHost = testHost,
						secondHost = testOtherHost,
						name = "gre0",
						virtualNetwork = testVirtualNetwork
				)
		)

		session.verifyCommandExecution("ovs-vsctl add-port.*".toRegex())
		assertEquals(updatedHostConfiguration!!.id, testHost.id)
		assertTrue {
			OvsGrePort(
					name = "gre0",
					remoteAddress = testOtherHost.address
			) in (updatedHostConfiguration!!.networkConfiguration.single() as OvsNetworkConfiguration).ports
		}
	}
}