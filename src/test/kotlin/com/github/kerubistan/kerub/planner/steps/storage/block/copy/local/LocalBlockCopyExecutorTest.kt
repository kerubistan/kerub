package com.github.kerubistan.kerub.planner.steps.storage.block.copy.local

import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

import java.util.UUID.randomUUID

class LocalBlockCopyExecutorTest {

	@Test
	fun execute() {
		val sourceDevice = testDisk.copy(id = randomUUID())
		val targetDevice = testDisk.copy(id = randomUUID())
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val session = mock<ClientSession>()
		doAnswer { invocation ->
			(invocation.arguments[1] as (ClientSession) -> Any).invoke(session)
		}.whenever(hostCommandExecutor).execute(eq(testHost), any<(ClientSession) -> Any>())
		session.mockCommandExecution("dd .*".toRegex())
		val stepExecutor = mock<StepExecutor<AbstractOperationalStep>>()
		LocalBlockCopyExecutor(hostCommandExecutor, stepExecutor).execute(
				LocalBlockCopy(
						sourceDevice = sourceDevice,
						targetDevice = targetDevice,
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
		)
	}
}