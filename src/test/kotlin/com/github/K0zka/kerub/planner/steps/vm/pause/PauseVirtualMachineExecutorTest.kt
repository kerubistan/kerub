package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.math.BigInteger
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class PauseVirtualMachineExecutorTest {

	@Mock
	var hostManager: HostManager? = null

	@Mock
	var hypervisor: Hypervisor? = null

	@Mock
	var vmDynDao: VirtualMachineDynamicDao? = null


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
		Mockito.`when`(hostManager!!.getHypervisor(eq(host))).thenReturn(hypervisor)
	}

	@Test
	fun execute() {
		Mockito.`when`(vmDynDao!!.get(vm.id)).thenReturn(vmDyn)
		PauseVirtualMachineExecutor(hostManager!!, vmDynDao!!).execute(PauseVirtualMachine(vm = vm, host = host))

		Mockito.verify(hypervisor)!!.suspend(eq(vm))
	}

}