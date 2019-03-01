package com.github.kerubistan.kerub.planner.steps.vm.pause

import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.hypervisor.Hypervisor
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
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
		whenever(vmDynDao[vm.id]).thenReturn(vmDyn)
		PauseVirtualMachineExecutor(hostManager, vmDynDao).execute(PauseVirtualMachine(vm = vm, host = host))

		verify(hypervisor).suspend(Mockito.eq(vm) ?: vm)
	}

}