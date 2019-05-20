package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.config.LvmPoolConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RemoveLvmPoolExecutorTest {
	@Test
	fun execute() {
		val vg1 = LvmStorageCapability(
				id = UUID.randomUUID(),
				size = 1.TB,
				physicalVolumes = mapOf("/dev/sda" to 1.TB),
				volumeGroupName = "vg-1"
		)
		val vg2 = LvmStorageCapability(
				id = UUID.randomUUID(),
				size = 2.TB,
				physicalVolumes = mapOf("/dev/sdb" to 2.TB),
				volumeGroupName = "vg-2"
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								vg1,
								vg2
						)
				)
		)
		val hostConfiguration = HostConfiguration(
				id = host.id,
				storageConfiguration = listOf(
						LvmPoolConfiguration(
								vgName = "vg-1",
								size = 100.GB,
								poolName = "pool-vg-1"
						),
						LvmPoolConfiguration(
								vgName = "vg-2",
								size = 500.GB,
								poolName = "pool-vg-2"
						)
				)
		)
		var updatedHostConfiguration : HostConfiguration? = null
		val hostDynamic = HostDynamic(
				id = host.id,
				status = HostStatus.Up,
				storageStatus = listOf(
						SimpleStorageDeviceDynamic(
								id = vg1.id,
								freeCapacity = 100.GB
						),
						SimpleStorageDeviceDynamic(
								id = vg2.id,
								freeCapacity = 1500.GB
						)
				)
		)
		var updatedHostDynamic : HostDynamic? = null
		val commandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		session.mockCommandExecution("lvm lvremove.*".toRegex())
		session.mockCommandExecution("lvm vgs.*".toRegex(),
				output = """  WfbuiJ-KniK-WBF9-h2ae-IwgM-k1Jh-671l51:vg-1:9139388416B:4194304B:2179:1
""")
		whenever(commandExecutor.execute(eq(host), any<(ClientSession) -> Any>())).then {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}
		val hostCfgDao = mock<HostConfigurationDao>()
		whenever(hostCfgDao.update(eq(host.id), any(), any())).then {
			updatedHostConfiguration = (it.arguments[2] as (HostConfiguration) -> HostConfiguration)
					.invoke(hostConfiguration)
			updatedHostConfiguration
		}
		val hostDynamicDao = mock<HostDynamicDao>()
		whenever(hostDynamicDao.update(eq(host.id), any(), any())).then {
			updatedHostDynamic = (it.arguments[2] as (HostDynamic) -> HostDynamic).invoke(hostDynamic)
			updatedHostDynamic
		}

		RemoveLvmPoolExecutor(commandExecutor, hostCfgDao, hostDynamicDao).execute(
				RemoveLvmPool(host = host, pool = "pool-vg-1", vgName = "vg-1")
		)

		assertEquals(4194304.toBigInteger(), updatedHostDynamic!!.storageStatus.single { it.id == vg1.id }.freeCapacity)
		assertTrue {
			updatedHostConfiguration!!.storageConfiguration.single()
					.let { it is LvmPoolConfiguration && it.vgName == "vg-2" }
		}
	}
}