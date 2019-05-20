package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.toInputStream
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.apache.commons.io.input.NullInputStream
import org.apache.sshd.client.channel.ChannelExec
import org.apache.sshd.client.future.OpenFuture
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class ExtendLvmPoolExecutorTest {

	private val hostCommandExecutor: HostCommandExecutor = mock()
	private val hostConfigDao: HostConfigurationDao = mock()
	private val hostDynDao: HostDynamicDao = mock()

	@Test
	fun update() {
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								LvmStorageCapability(
										volumeGroupName = "test-vg",
										size = 2.TB,
										physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB)
								)
						)
				)
		)

		//TODO can't mock hostDynDao.update()

		ExtendLvmPoolExecutor(hostCommandExecutor, hostConfigDao, hostDynDao).update(
				ExtendLvmPool(host = host, vgName = "test-vg", pool = "test-pool", addSize = 10.GB),
				1.TB to 128.GB
		)

		//TODO and then there is nothing to verify

	}

	@Test
	fun perform() {

		val clientSession = mock<ClientSession>()
		val channel : ChannelExec = mock()
		val future : OpenFuture = mock()
		whenever(clientSession.createExecChannel(any())).thenReturn(channel)
		whenever(channel.open()).thenReturn(future)
		whenever(channel.invertedErr).then { NullInputStream(0) }
		whenever(channel.invertedOut).then {
			"".toInputStream()
		}
				.then {
					"""uPPT5K-Rtym-cxQX-f3iu-oiZf-M4Z3-t8v4We:test-vg:4286578688B:1065353216B:1022:254
""".toInputStream()
				}.then {
			"""la6xp4-En1K-fkhX-0Zus-PWp7-1cat-mBXKUk:test-pool::21474836480B:::thin,pool:0.00
""".toInputStream()
		}
		doAnswer {
			val callback = it.arguments[1] as (ClientSession) -> Pair<BigInteger, BigInteger>
			callback(clientSession)
		} .whenever(hostCommandExecutor)
				.execute(eq(testHost), any<(ClientSession) -> Pair<BigInteger, BigInteger>>())

		val updates = ExtendLvmPoolExecutor(hostCommandExecutor, hostConfigDao, hostDynDao).perform(
				ExtendLvmPool(host = testHost, vgName = "test-vg", pool = "test-pool", addSize = 10.GB)
		)

		verify(hostCommandExecutor).execute(eq(testHost), any<(ClientSession) -> Pair<BigInteger, BigInteger>>())

		assertEquals(1065353216.toBigInteger(), updates.first)
		assertEquals(21474836480.toBigInteger(), updates.second)
	}

}