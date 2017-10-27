package com.github.kerubistan.kerub.planner.steps.vm.stop

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.VirtualMachine
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import java.math.BigInteger
import java.util.UUID

class StopVirtualMachineExecutorTest {
	val hostManager: HostManager = mock()
	val hypervisor: Hypervisor = mock()
	val vmDynDao: VirtualMachineDynamicDao = mock()

	@Test
	fun execute() {
		val host = Host(
				address = "127.0.0.1",
				publicKey = "",
				dedicated = false
		)

		val vm = VirtualMachine(
				name = "",
				id = UUID.randomUUID(),
				nrOfCpus = 1,
				memory = Range(BigInteger("128"), BigInteger("256"))
		)

		val step = StopVirtualMachine(
				host = host,
				vm = vm
		)

		whenever(hostManager.getHypervisor(any<Host>())).thenReturn(hypervisor)

		StopVirtualMachineExecutor(hostManager, vmDynDao).execute(step)

		verify(hypervisor).stopVm(eq(vm) ?: vm)
	}
}