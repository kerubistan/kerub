package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecutionSequence
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LogicalVolume
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigInteger
import java.util.UUID
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class CreateLvmPoolExecutorTest {

	@Test
	fun prepare() {
		assertThrows<IllegalStateException>("requesting more space than what is available") {
			simulateWithCapability(vgFree = 128.GB, poolSize = 512.GB)
		}
		assertThrows<IllegalStateException>("requesting all available (nothing left for meta)") {
			simulateWithCapability(vgFree = 512.GB, poolSize = 512.GB)
		}
		assertThrows<IllegalStateException>("requesting almos all available (not enough left for meta)") {
			simulateWithCapability(vgFree = 512.GB, poolSize = 511.GB)
		}

		// should work
		simulateWithCapability(vgFree = 512.GB, poolSize = 400.GB)

	}

	private fun simulateWithCapability(vgFree: BigInteger, poolSize: BigInteger) {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		val lvmStorageCapability = LvmStorageCapability(
				volumeGroupName = "vg-1",
				size = 1.TB,
				id = randomUUID(),
				physicalVolumes = mapOf("/dev/sda" to 1.TB)
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(
								BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)
						),
						storageCapabilities = listOf(
								lvmStorageCapability
						)
				)
		)
		hostCommandExecutor.mockHost(host, session)
		session.mockCommandExecution(
				"lvm vgs.*".toRegex(),
				output = "uPPT5K-Rtym-cxQX-f3iu-oiZf-M4Z3-t8v4We:${lvmStorageCapability.volumeGroupName}" +
						":${lvmStorageCapability.size}B:${vgFree}B:1022:254\n")
		CreateLvmPoolExecutor(hostCommandExecutor, mock()).prepare(
				CreateLvmPool(
						host = host,
						vgName = lvmStorageCapability.volumeGroupName,
						size = poolSize,
						name = randomUUID().toString()
				)
		)
	}

	@Test
	fun perform() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostCfgDao = mock<HostConfigurationDao>()
		val session = mock<ClientSession>()
		val lvmStorageCapability = LvmStorageCapability(
				volumeGroupName = "vg-1",
				size = 1.TB,
				id = randomUUID(),
				physicalVolumes = mapOf("/dev/sda" to 1.TB)
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(
								BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)
						),
						storageCapabilities = listOf(
								lvmStorageCapability
						)
				)
		)
		val hostCfg = HostConfiguration(
				id = host.id
		)

		whenever(hostCfgDao[host.id]).thenReturn(hostCfg)
		session.mockCommandExecutionSequence(
				".*".toRegex(), outputs = listOf(
				"\n",
				"la6xp4-En1K-fkhX-0Zus-PWp7-1cat-mBXKUk:test-pool::21474836480B:::thin,pool:0.00\n"
		))
		hostCommandExecutor.mockHost(host, session)

		val update = CreateLvmPoolExecutor(hostCommandExecutor, hostCfgDao).perform(
				CreateLvmPool(
						vgName = lvmStorageCapability.volumeGroupName,
						size = 12.GB,
						host = host,
						name = "test-pool"
				)
		)
		assertEquals("test-pool", update.name)
	}

	@Test
	fun update() {
		val lvmStorageCapability = LvmStorageCapability(
				volumeGroupName = "vg-1",
				size = 1.TB,
				id = randomUUID(),
				physicalVolumes = mapOf("/dev/sda" to 1.TB)
		)
		val host = testHost.copy(
			capabilities = testHostCapabilities.copy(
					blockDevices = listOf(
							BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB)
					),
					storageCapabilities = listOf(
							lvmStorageCapability
					)
			)
		)
		val hostCfg = HostConfiguration(
				id = host.id
		)

		val hostCfgDao = mock<HostConfigurationDao>()

		whenever(hostCfgDao[eq(host.id)]).thenReturn(hostCfg)
		doAnswer {
			val cfg = (it.arguments[1] as (UUID) -> HostConfiguration).invoke(it.arguments[0] as UUID)
			(it.arguments[2] as (HostConfiguration) -> HostConfiguration).invoke(cfg)
		}.whenever(hostCfgDao).update(eq(host.id), any(), any())

		CreateLvmPoolExecutor(mock(), hostCfgDao).update(
				CreateLvmPool(
						vgName = lvmStorageCapability.volumeGroupName,
						size = 12.GB,
						host = host,
						name = "test-pool"
				),
				LogicalVolume(
						id = "la6xp4-En1K-fkhX-0Zus-PWp7-1cat-mBXKUk",
						size = 12.GB,
						path = "",
						name = "test-pool",
						layout = listOf(),
						dataPercent = null,
						maxRecovery = null,
						minRecovery = null
				)
		)

		verify(hostCfgDao).update(eq(host.id), any(), any())
	}

}