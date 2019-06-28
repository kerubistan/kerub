package com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import java.util.UUID.randomUUID

class RemoteBlockCopyExecutorTest {

	@Test
	fun execute() {
		val sourceDevice = testDisk.copy(id = randomUUID())
		val targetDevice = testDisk.copy(id = randomUUID())
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val stepExecutor = mock<StepExecutor<AbstractOperationalStep>>()
		val step = RemoteBlockCopy(
				sourceDevice = sourceDevice,
				targetDevice = targetDevice,
				sourceHost = testHost.copy(id = randomUUID()),
				allocationStep = CreateLv(
						host = testHost,
						capability = testLvmCapability,
						disk = testDisk
				),
				sourceAllocation = VirtualStorageLvmAllocation(
						hostId = testHost.id,
						vgName = testLvmCapability.volumeGroupName,
						actualSize = testDisk.size,
						path = "",
						capabilityId = testLvmCapability.id
				)
		)
		RemoteBlockCopyExecutor(hostCommandExecutor, stepExecutor).execute(step)

		verify(stepExecutor).execute(eq(step.allocationStep))
	}
}