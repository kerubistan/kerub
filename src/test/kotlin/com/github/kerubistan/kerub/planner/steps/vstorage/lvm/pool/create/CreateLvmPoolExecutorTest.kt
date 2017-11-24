package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.pool.create

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.toInputStream
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class CreateLvmPoolExecutorTest {

	@Test
	fun perform() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostCfgDao = mock<HostConfigurationDao>()
		val session = mock<ClientSession>()
		val execChannel = mock<ChannelExec>()
		val future = mock<OpenFuture>()
		val host = testHost.copy(

		)
		val hostCfg = HostConfiguration(
				id = host.id
		)

		whenever(hostCfgDao[host.id]).thenReturn(hostCfg)
		whenever(session.createExecChannel(any())).thenReturn(execChannel)
		whenever(execChannel.open()).thenReturn(future)
		whenever(execChannel.invertedErr).then { NullInputStream(0) }
		whenever(execChannel.invertedOut)
				.then { "\n".toInputStream() }
				.then { "la6xp4-En1K-fkhX-0Zus-PWp7-1cat-mBXKUk:test-pool::21474836480B:::thin,pool:0.00\n".toInputStream() }

		doAnswer { (it.arguments[1] as (ClientSession) -> LogicalVolume).invoke(session) }.whenever(hostCommandExecutor).execute(
				host = eq(host),
				closure = any<(ClientSession) -> LogicalVolume>()
		)

		val update = CreateLvmPoolExecutor(hostCommandExecutor, hostCfgDao).perform(
				CreateLvmPool(
						vgName = "test-vg",
						size = 12.GB,
						host = host,
						name = "test-pool"
				)
		)
		assertEquals("test-pool", update.name)
	}

	@Test
	fun update() {
		val host = testHost.copy(

		)
		val hostCfg = HostConfiguration(
				id = host.id
		)

		val hostCfgDao = mock<HostConfigurationDao>()

		whenever(hostCfgDao[eq(host.id)]).thenReturn(hostCfg)
		doAnswer {
			val cfg = (it.arguments[1] as (UUID) -> HostConfiguration).invoke(it.arguments[0] as UUID)
			(it.arguments[2] as (HostConfiguration) -> HostConfiguration).invoke(cfg)
		} .whenever(hostCfgDao).update(eq(host.id), any(), any())

		CreateLvmPoolExecutor(mock(), hostCfgDao).update(
				CreateLvmPool(
						vgName = "test-vg",
						size = 12.GB,
						host = host,
						name = "test-pool"
				),
				LogicalVolume(
						id = "la6xp4-En1K-fkhX-0Zus-PWp7-1cat-mBXKUk",
						size = 12.GB,
						path = "",
						name = "test-pool",
						layout = "",
						dataPercent = null,
						maxRecovery = null,
						minRecovery = null
				)
		)

		verify(hostCfgDao).update(eq(host.id), any(), any())
	}

}