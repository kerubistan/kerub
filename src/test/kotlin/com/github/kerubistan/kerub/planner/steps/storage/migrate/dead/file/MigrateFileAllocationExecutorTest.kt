package com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file

import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFs
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.github.kerubistan.kroki.size.TB
import org.apache.sshd.client.session.ClientSession
import org.junit.Test
import java.util.UUID

class MigrateFileAllocationExecutorTest {

	@Test
	fun execute() {
		val sourceCapability = FsStorageCapability(
				id = UUID.randomUUID(),
				mountPoint = "/kerub",
				fsType = "ext4",
				size = 1.TB
		)
		val targetCapability = FsStorageCapability(
				id = UUID.randomUUID(),
				mountPoint = "/kerub",
				fsType = "ext4",
				size = 1.TB
		)
		val sourceHost = testHost.copy(
				id = UUID.randomUUID(),
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(sourceCapability)
				)
		)
		val targetHost = testHost.copy(
				id = UUID.randomUUID(),
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(targetCapability)
				)
		)
		val sourceAllocation = VirtualStorageFsAllocation(
				capabilityId = sourceCapability.id,
				mountPoint = sourceCapability.mountPoint,
				type = VirtualDiskFormat.qcow2,
				fileName = "${testDisk.id}.qcow2",
				actualSize = testDisk.size,
				hostId = sourceHost.id
		)

		val hostCommandExecutor = mock<HostCommandExecutor>()
		val virtualStorageDeviceDynamicDao = mock<VirtualStorageDeviceDynamicDao>()
		val stepExecutor = mock<StepExecutor<AbstractOperationalStep>>()
		val sourceSession = mock<ClientSession>()
		hostCommandExecutor.mockHost(sourceHost, sourceSession)
		sourceSession.mockCommandExecution(".*dd .*".toRegex())
		val allocationStep = CreateImage(
				host = targetHost,
				disk = testDisk,
				capability = targetCapability,
				format = VirtualDiskFormat.qcow2
		)
		val deAllocationStep = UnAllocateFs(
				vstorage = testDisk,
				host = sourceHost,
				allocation = sourceAllocation
		)
		MigrateFileAllocationExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao, stepExecutor)
				.execute(
						MigrateFileAllocation(
								sourceHost = sourceHost,
								targetHost = targetHost,
								sourceAllocation = sourceAllocation,
								virtualStorage = testDisk,
								allocationStep = allocationStep,
								deAllocationStep = deAllocationStep
						)
				)

		sourceSession.verifyCommandExecution(".*dd.*".toRegex())
		verify(stepExecutor).execute(eq(allocationStep))
		verify(stepExecutor).execute(eq(deAllocationStep))
	}
}