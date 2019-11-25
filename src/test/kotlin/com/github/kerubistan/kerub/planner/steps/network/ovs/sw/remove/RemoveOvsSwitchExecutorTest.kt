package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class RemoveOvsSwitchExecutorTest {
	@Test
	fun execute() {
		val hostConfigurationDao = mock<HostConfigurationDao>()
		val commandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		commandExecutor.mockHost(testHost, session)
		session.mockCommandExecution("ovs-vsctl del-br.*".toRegex())

		var hostId: UUID? = null
		var updatedConfig: HostConfiguration? = null
		doAnswer {
			hostId = it.arguments[0] as UUID
			updatedConfig = (it.arguments[2] as (HostConfiguration) -> HostConfiguration)(
					HostConfiguration(
							id = hostId!!,
							networkConfiguration = listOf(
									OvsNetworkConfiguration(virtualNetworkId = testVirtualNetwork.id)
							)
					)
			)
		}.whenever(hostConfigurationDao).update(any(), any(), any())

		RemoveOvsSwitchExecutor(hostCommandExecutor = commandExecutor, hostConfigurationDao = hostConfigurationDao).execute(
				RemoveOvsSwitch(host = testHost, virtualNetwork = testVirtualNetwork)
		)

		session.verifyCommandExecution("ovs-vsctl del-br.*".toRegex())
		assertEquals(testHost.id, hostId)
		assertEquals(testHost.id, updatedConfig!!.id)
	}
}