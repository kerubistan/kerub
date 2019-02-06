package com.github.kerubistan.kerub.planner.steps.storage.lvm.vg

import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertTrue

class RemoveDiskFromVGExecutorTest {

	@Test
	fun execute() {
		val hostDao = mock<HostDao>()
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()

		val capability = testLvmCapability.copy(
				physicalVolumes = testLvmCapability.physicalVolumes + ("/dev/sdb" to 2.TB) + ("/dev/sdd" to 1.TB),
				size = 4.TB
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(
								BlockDevice("/dev/sda1", 1.TB),
								BlockDevice("/dev/sdb", 2.TB),
								BlockDevice("/dev/sdd", 1.TB)
						),
						storageCapabilities = listOf(capability)
				)
		)

		session.mockCommandExecution("lvm pvmove.*".toRegex())
		session.mockCommandExecution("lvm vgreduce.*".toRegex())
		session.mockCommandExecution(
				"lvm vgs.*".toRegex(),
				"""  RsDNYC-Un0h-QvhF-hMqe-dEny-Bjlg-qbeeZa:test-vg:433800085504B:4194304B:103426:1
""")

		doAnswer {
			val update = it.arguments[1] as (ClientSession) -> BigInteger
			update(session)
		}.whenever(hostCommandExecutor).execute(any(), any<(ClientSession) -> Unit>())

		var updatedHost: Host? = null
		doAnswer {
			val change = it.arguments[2] as (Host) -> Host
			updatedHost = change(host)
			updatedHost
		}.whenever(hostDao).update(id = eq(host.id), retrieve = any(), change = any())

		RemoveDiskFromVGExecutor(hostCommandExecutor = hostCommandExecutor, hostDao = hostDao).execute(
				RemoveDiskFromVG(
						capability = capability,
						device = "/dev/sdd",
						host = host
				)
		)

		session.verifyCommandExecution("lvm pvmove.*".toRegex())
		session.verifyCommandExecution("lvm vgreduce.*".toRegex())
		session.verifyCommandExecution("lvm vgs.*".toRegex())
		assertTrue { updatedHost!!.capabilities!!.storageCapabilitiesById[capability.id]!!.size < capability.size }
	}
}