package com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.mockHost
import com.github.kerubistan.kerub.sshtestutils.mockCommandExecution
import com.github.kerubistan.kerub.sshtestutils.verifyCommandExecution
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.atLeast
import com.nhaarman.mockito_kotlin.mock
import org.apache.sshd.client.session.ClientSession
import org.junit.Test

class VirtualBoxStartVirtualMachineExecutorTest {

	@Test
	fun execute() {
		val hostCommandExecutor = mock<HostCommandExecutor>()
		val vmDynDao = mock<VirtualMachineDynamicDao>()
		val session = mock<ClientSession>()
		session.mockCommandExecution("VBoxManage .*".toRegex())
		hostCommandExecutor.mockHost(testHost, session)
		VirtualBoxStartVirtualMachineExecutor(hostCommandExecutor, vmDynDao).execute(
				VirtualBoxStartVirtualMachine(
						host = testHost,
						vm = testVm
				)
		)
		session.verifyCommandExecution("VBoxManage .*".toRegex(), atLeast(1))
	}
}