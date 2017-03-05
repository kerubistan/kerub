package com.github.K0zka.kerub.planner.steps.vm.migrate.kvm

import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.hypervisor.Hypervisor
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.mockito.ArgumentMatchers

class KvmMigrateVirtualMachineExecutorTest {
	val hostManager: HostManager = mock()
	val hypervisor: Hypervisor = mock()

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
		whenever(hostManager.getHypervisor(any<Host>())).thenReturn(hypervisor)
		KvmMigrateVirtualMachineExecutor(hostManager).execute(KvmMigrateVirtualMachine(
				vm = vm,
				source = source,
				target = target
		                                                                          ))

		verify(hypervisor).migrate(
				vm = ArgumentMatchers.eq(vm) ?: vm,
				source = eq(source),
		        target = eq(target)
		                                   )
	}
}