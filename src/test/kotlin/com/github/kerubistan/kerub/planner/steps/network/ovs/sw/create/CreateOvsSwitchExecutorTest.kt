package com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create

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
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CreateOvsSwitchExecutorTest {

	val session = mock<ClientSession>()

	@Test
	fun execute() {
		val commandExecutor = mock<HostCommandExecutor>()
		val configDao = mock<HostConfigurationDao>()
		commandExecutor.mockHost(testHost, session)
		session.mockCommandExecution("^ovs-vsctl add-br .*".toRegex())
		var updatedConfig: HostConfiguration? = null
		doAnswer {
			val defaultValue = it.arguments[1] as () -> HostConfiguration
			val change = it.arguments[2] as (HostConfiguration) -> HostConfiguration
			updatedConfig = change(defaultValue())
		}.whenever(configDao).updateWithDefault(eq(testHost.id), defaultValue = any(), change = any())

		CreateOvsSwitchExecutor(
				hostCommandExecutor = commandExecutor,
				hostConfigurationDao = configDao
		).execute(
				CreateOvsSwitch(
						host = testHost,
						network = testVirtualNetwork
				)
		)

		session.verifyCommandExecution("^ovs-vsctl add-br .*".toRegex())
		assertEquals(testVirtualNetwork.id, (updatedConfig!!.networkConfiguration.first() as OvsNetworkConfiguration).virtualNetworkId)
	}
}