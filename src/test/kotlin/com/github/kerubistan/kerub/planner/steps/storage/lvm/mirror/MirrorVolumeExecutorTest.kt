package com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import kotlin.test.assertTrue

class MirrorVolumeExecutorTest {

	@ExperimentalUnsignedTypes
	@Test
	fun execute() {
		val session = mock<ClientSession>()
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val hostDynamicDao = mock<HostDynamicDao>()
		val virtualStorageDeviceDynamicDao = mock<VirtualStorageDeviceDynamicDao>()

		val lvmStorageCapability = LvmStorageCapability(
				size = 8.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf(
						"/dev/sda" to 1.TB,
						"/dev/sdb" to 1.TB
				)
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						blockDevices = listOf(
								BlockDevice("/dev/sda", 1.TB),
								BlockDevice("/dev/sdb", 1.TB)
						),
						storageCapabilities = listOf(
								lvmStorageCapability
						)
				)
		)
		val hostDynamic = hostUp(testHost).copy(
				storageStatus = listOf(
						CompositeStorageDeviceDynamic(
								id = lvmStorageCapability.id,
								reportedFreeCapacity = 1.TB
						)
				)
		)

		var updatedDynamic : HostDynamic? = null
		var updatedVirtualStorageDynamic : VirtualStorageDeviceDynamic? = null

		session.mockCommandExecution("lvm lvconvert.*".toRegex())
		session.mockCommandExecution("lvm lvs.*".toRegex(), "  Mvd5u6-pTbR-SUS2-sd2l-kx41-a0bx-YGuWcK:${testDisk.id}:/dev/vg-1/${testDisk.id}:9135194112B:::linear:\n")
		session.mockCommandExecution("lvm vgs.*".toRegex(),"  WfbuiJ-KniK-WBF9-h2ae-IwgM-k1Jh-671l51:vg-1:9139388416B:4194304B:2179:1\n")

		doAnswer {
			(it.arguments[1] as (ClientSession) -> Any).invoke(session)
		}.whenever(hostCommandExecutor).execute(eq(host), any<(ClientSession) -> Any>())

		doAnswer {
			updatedDynamic = (it.arguments[2] as (HostDynamic) -> HostDynamic).invoke(hostDynamic)
		}.whenever(hostDynamicDao).update(id = eq(host.id), retrieve = any(), change = any())

		doAnswer {
			updatedVirtualStorageDynamic = (it.arguments[2] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic).invoke(
					VirtualStorageDeviceDynamic(
							id = testDisk.id,
							allocations = listOf(
									VirtualStorageLvmAllocation(
											capabilityId = lvmStorageCapability.id,
											vgName = lvmStorageCapability.volumeGroupName,
											hostId = host.id,
											actualSize = testDisk.size,
											mirrors = 0,
											path = ""
									)
							)
					)
			)
		}.whenever(virtualStorageDeviceDynamicDao).update(id = eq(testDisk.id), retrieve = any(), change = any())

		MirrorVolumeExecutor(hostCommandExecutor, hostDynamicDao, virtualStorageDeviceDynamicDao).execute(
				MirrorVolume(
						host = host,
						vStorage = testDisk,
						capability = lvmStorageCapability,
						mirrors = 1.toUShort(),
						allocation = VirtualStorageLvmAllocation(
								capabilityId = lvmStorageCapability.id,
								mirrors = 0,
								hostId = host.id,
								vgName = lvmStorageCapability.volumeGroupName,
								path = "",
								actualSize = 1.GB
						)
				)
		)

		assertTrue("virtual storage dynamic updated") {
			(updatedVirtualStorageDynamic!!.allocations.single() as VirtualStorageLvmAllocation).mirrors == 1.toByte()
		}

		assertTrue("host dynamic updated") {
			// see the output of vgs
			(updatedDynamic!!.storageStatus.single()).freeCapacity == 4194304.toBigInteger()
		}

	}
}