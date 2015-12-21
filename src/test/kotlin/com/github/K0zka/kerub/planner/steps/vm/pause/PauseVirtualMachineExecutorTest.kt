package com.github.K0zka.kerub.planner.steps.vm.pause

import com.github.K0zka.kerub.eq
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class PauseVirtualMachineExecutorTest {

	@Mock
	var hostManager: HostManager? = null

	@Mock
	var hypervisor: Hypervisor? = null

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


	@Before
	fun setup() {
		Mockito.`when`(hostManager!!.getHypervisor(eq(host))).thenReturn(hypervisor)
	}

	@Test
	fun execute() {
		PauseVirtualMachineExecutor(hostManager!!).execute(PauseVirtualMachine(vm = vm, host = host))

		Mockito.verify(hypervisor)!!.suspend(eq(vm))
	}
}