package com.github.K0zka.kerub.planner.steps.vm.start

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.jvm.java

@RunWith(MockitoJUnitRunner::class)
class StartVirtualMachineExecutorTest {
	@Mock
	var hostManager : HostManager? = null
	@Mock
	var hypervisor : Hypervisor? = null

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

		Mockito.`when`(hostManager!!.getHypervisor(Matchers.any(Host::class.java) ?: host)).thenReturn(hypervisor)

		StartVirtualMachineExecutor(hostManager!!).execute(step)

		Mockito.verify(hypervisor!!).startVm(Matchers.eq(vm) ?: vm)
	}

}