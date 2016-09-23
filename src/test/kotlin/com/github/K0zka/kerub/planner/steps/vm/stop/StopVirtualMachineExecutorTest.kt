package com.github.K0zka.kerub.planner.steps.vm.stop

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.mockito.Matchers
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

		verify(hypervisor).stopVm(Matchers.eq(vm) ?: vm)
	}
}