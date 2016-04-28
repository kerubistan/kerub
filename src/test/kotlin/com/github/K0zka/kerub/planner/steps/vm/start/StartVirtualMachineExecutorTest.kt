package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.display.RemoteConsoleProtocol
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.mockito.Mockito
import java.math.BigInteger
import java.util.UUID

class StartVirtualMachineExecutorTest {
	var hostManager: HostManager = mock()

	var vmDynDao: VirtualMachineDynamicDao = mock()

	var hypervisor: Hypervisor = mock()

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

		val step = StartVirtualMachine(
				host = host,
				vm = vm
		)

		whenever(hostManager.getHypervisor(any())).thenReturn(hypervisor)
		whenever(hypervisor.getDisplay(Mockito.any(VirtualMachine::class.java) ?: vm)).thenReturn(RemoteConsoleProtocol.spice to 5900)

		StartVirtualMachineExecutor(hostManager, vmDynDao).execute(step)

		verify(hypervisor).startVm(
				//TODO: eq is failing with VM in kotlin-mockito (test framework bug)
				Mockito.eq(vm) ?: vm,
				any()
		)
	}

}