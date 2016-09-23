package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.util.UUID

class PauseVirtualMachineExecutorTest {

	val hostManager: HostManager = mock()
	var hypervisor: Hypervisor = mock()
	var vmDynDao: VirtualMachineDynamicDao = mock()


	val vm = VirtualMachine(
			id = UUID.randomUUID(),
			name = "vm-1"
	)

	val host = Host(
			id = UUID.randomUUID(),
			address = "host-1",
			publicKey = "",
			dedicated = true
	)

	val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			hostId = host.id,
			status = VirtualMachineStatus.Up,
			memoryUsed = BigInteger.ZERO
	)

	@Before
	fun setup() {
		whenever(hostManager.getHypervisor(eq(host))).thenReturn(hypervisor)
	}

	@Test
	fun execute() {
		whenever(vmDynDao.get(vm.id)).thenReturn(vmDyn)
		PauseVirtualMachineExecutor(hostManager, vmDynDao).execute(PauseVirtualMachine(vm = vm, host = host))

		verify(hypervisor).suspend(eq(vm))
	}

}