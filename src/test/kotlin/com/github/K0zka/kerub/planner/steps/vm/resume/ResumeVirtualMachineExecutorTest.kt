package com.github.K0zka.kerub.planner.steps.vm.resume

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.math.BigInteger
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class ResumeVirtualMachineExecutorTest{
	@Mock
	var hostManager: HostManager? = null

	@Mock
	var hypervisor: Hypervisor? = null

	@Mock
	var vmDynDao : VirtualMachineDynamicDao? = null

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
			status = VirtualMachineStatus.Paused,
			memoryUsed = BigInteger.ZERO
	)

	@Before
	fun setup() {
		Mockito.`when`(hostManager!!.getHypervisor(eq(host))).thenReturn(hypervisor)
	}

	@Test
	fun execute() {
		Mockito.`when`(vmDynDao!!.get(vm.id)).thenReturn(vmDyn)
		ResumeVirtualMachineExecutor(hostManager!!, vmDynDao!!).execute(ResumeVirtualMachine(vm, host))

		Mockito.verify(hypervisor)!!.resume(eq(vm))
	}

	@Test
	fun executeWithoutData() {
		Mockito.`when`(vmDynDao!!.get(vm.id)).thenReturn(vmDyn)
		ResumeVirtualMachineExecutor(hostManager!!, vmDynDao!!).execute(ResumeVirtualMachine(vm, host))

		Mockito.verify(hypervisor)!!.resume(eq(vm))
	}

}