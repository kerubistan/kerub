package com.github.kerubistan.kerub.planner.steps.vm.resume

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

class ResumeVirtualMachineExecutorTest{
	val hostManager: HostManager = mock()
	val hypervisor: Hypervisor = mock()
	private val vmDynDao : VirtualMachineDynamicDao = mock()

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

	private val vmDyn = VirtualMachineDynamic(
			id = vm.id,
			hostId = host.id,
			status = VirtualMachineStatus.Paused,
			memoryUsed = BigInteger.ZERO
	)

	@Before
	fun setup() {
		whenever(hostManager.getHypervisor(eq(host))).thenReturn(hypervisor)
	}

	@Test
	fun execute() {
		whenever(vmDynDao[eq(vm.id)]).thenReturn(vmDyn)
		ResumeVirtualMachineExecutor(hostManager, vmDynDao).execute(ResumeVirtualMachine(vm, host))

		verify(hypervisor).resume(Mockito.eq(vm) ?: vm)
	}

	@Test
	fun executeWithoutData() {
		whenever(vmDynDao[vm.id]).thenReturn(vmDyn)
		ResumeVirtualMachineExecutor(hostManager, vmDynDao).execute(ResumeVirtualMachine(vm, host))

		verify(hypervisor).resume(Mockito.eq(vm) ?: vm)
	}

}