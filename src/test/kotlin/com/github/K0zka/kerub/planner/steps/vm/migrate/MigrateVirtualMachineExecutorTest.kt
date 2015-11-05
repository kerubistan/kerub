package com.github.K0zka.kerub.planner.steps.vm.migrate

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
public class MigrateVirtualMachineExecutorTest {
	@Mock
	var hostManager: HostManager? = null
	@Mock
	var hypervisor: Hypervisor? = null

	@Test
	fun execute() {
		val source = Host(
				address = "source-host.example.com",
				dedicated = true,
				publicKey = ""
		                 )
		val target = Host(
				address = "target-host.example.com",
				dedicated = true,
				publicKey = ""
		                 )
		val vm = VirtualMachine(
				name = "vm-1"
		                       )
		Mockito.`when`(hostManager!!.getHypervisor(Matchers.any(Host::class.java) ?: target)).thenReturn(hypervisor!!)
		MigrateVirtualMachineExecutor(hostManager!!).execute(MigrateVirtualMachine(
				vm = vm,
				source = source,
				target = target
		                                                                          ))

		Mockito.verify(hypervisor!!).migrate(
				vm = Matchers.eq(vm) ?: vm,
				source = Matchers.eq(source) ?: source,
		        target = Matchers.eq(target) ?: target
		                                   )
	}
}