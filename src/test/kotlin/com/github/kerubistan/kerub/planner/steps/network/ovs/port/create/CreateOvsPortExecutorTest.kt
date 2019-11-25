package com.github.kerubistan.kerub.planner.steps.network.ovs.port.create

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVirtualNetwork
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

internal class CreateOvsPortExecutorTest {
	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		hostCommandExecutor.mockHost(testHost, session)
		val hostConfigurationDao = mock<HostConfigurationDao>()
		session.mockCommandExecution("ovs-vsctl add-port.*".toRegex())

		var updatedHostConfiguration: HostConfiguration? = null
		doAnswer {
			val hostId = it.arguments[0] as UUID
			val update = it.arguments[2] as (HostConfiguration) -> HostConfiguration
			updatedHostConfiguration = update(
					HostConfiguration(
							id = hostId,
							networkConfiguration = listOf(
									OvsNetworkConfiguration(
											virtualNetworkId = testVirtualNetwork.id,
											ports = listOf()
									)
							)
					)
			)
		}.whenever(hostConfigurationDao).update(eq(testHost.id), any(), any())

		CreateOvsPortExecutor(hostCommandExecutor, hostConfigurationDao).execute(
				CreateOvsPort(
						host = testHost,
						portName = "TEST-PORT",
						virtualNetwork = testVirtualNetwork
				)
		)

		assertEquals(
				listOf(OvsDataPort(name = "TEST-PORT")),
				updatedHostConfiguration!!.index.ovsNetworkConfigurations.getValue(testVirtualNetwork.id).ports
		)

	}
}