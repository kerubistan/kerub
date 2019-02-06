package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.TB
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLv
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals

class MigrateBlockAllocationExecutorTest {

	@Test
	fun execute() {
		val sourceSession = mock<ClientSession>()
		val targetSession = mock<ClientSession>()
		val commandExecutor = mock<HostCommandExecutor>()
		val vssDynDao = mock<VirtualStorageDeviceDynamicDao>()
		val hostDynDao = mock<HostDynamicDao>()

		val sourceCapability = LvmStorageCapability(
				size = 1.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf("/dev/sdb" to 1.TB, "/dev/sdc" to 1.TB)
		)
		val sourceHost = testHost.copy(
				id = UUID.randomUUID(),
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(sourceCapability),
						blockDevices = listOf(
								BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB),
								BlockDevice(deviceName = "/dev/sdc", storageCapacity = 1.TB)
						)
				)
		)
		val targetCapability = LvmStorageCapability(
				size = 1.TB,
				volumeGroupName = "vg-1",
				physicalVolumes = mapOf("/dev/sdb" to 2.GB, "/dev/sdc" to 2.TB)
		)
		val targetHost = testHost.copy(
				id = UUID.randomUUID(),
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(targetCapability),
						blockDevices = listOf(
								BlockDevice(deviceName = "/dev/sdb", storageCapacity = 2.TB),
								BlockDevice(deviceName = "/dev/sdc", storageCapacity = 2.TB)
						)
				)
		)

		val sourceAllocation = VirtualStorageLvmAllocation(
				hostId = sourceHost.id,
				path = "",
				actualSize = testDisk.size,
				capabilityId = sourceCapability.id,
				vgName = sourceCapability.volumeGroupName
		)

		doAnswer {
			(it.arguments[1] as (ClientSession) -> Any).invoke(sourceSession)
		}.whenever(commandExecutor).execute(eq(sourceHost), any<(ClientSession) -> Any>())

		doAnswer {
			(it.arguments[1] as (ClientSession) -> Any).invoke(targetSession)
		}.whenever(commandExecutor).execute(eq(targetHost), any<(ClientSession) -> Any>())

		var dyn : VirtualStorageDeviceDynamic? = null

		doAnswer {
			dyn = (it.arguments[2] as (VirtualStorageDeviceDynamic) -> VirtualStorageDeviceDynamic).invoke(
					VirtualStorageDeviceDynamic(id = testDisk.id, allocations = listOf(sourceAllocation))
			)
			dyn
		}.whenever(vssDynDao).update(id = any(), retrieve = any(), change = any())

		targetSession.mockCommandExecution("lvm lvcreate.*".toRegex())
		targetSession.mockCommandExecution("lvm lvs.*".toRegex(), """  eCuTKA-rIDz-dzJq-48pK-DtqJ-X77p-YStcLS:${testDisk.id}:/dev/test/${testDisk.id}:1073741824B:::linear:
""")
		sourceSession.mockCommandExecution("bash -c.*".toRegex())
		sourceSession.mockCommandExecution("lvm lvremove.*".toRegex())

		MigrateBlockAllocationExecutor(commandExecutor, vssDynDao, hostDynDao).execute(
				MigrateBlockAllocation(
						sourceHost = sourceHost,
						targetHost = targetHost,
						virtualStorage = testDisk,
						sourceAllocation = sourceAllocation,
						allocationStep = CreateLv(
								host = targetHost,
								disk = testDisk,
								capability = targetCapability
						),
						deAllocationStep = UnAllocateLv(
								host = sourceHost,
								allocation = sourceAllocation,
								vstorage = testDisk
						)
				)
		)

		assertEquals(targetHost.id, dyn?.allocations?.single()?.hostId)

	}
}