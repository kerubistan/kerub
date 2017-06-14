package com.github.K0zka.kerub.planner.steps.vm.start.kvm

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.planner.OperationalState
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KvmStartVirtualMachineTest {
	@Test
	fun take() {
		val host = Host(
				address = "host-1.example.com",
				dedicated = true,
				publicKey = "test"
		)
		val hostDyn = HostDynamic(
				id = host.id,
				status = HostStatus.Up
		)
		val vm = VirtualMachine(
				name = "text-vm"
		)
		val originalState = OperationalState.fromLists(
				vms = listOf(vm),
				hosts = listOf(host),
				hostDyns = listOf(hostDyn)
		)

		val transformed = KvmStartVirtualMachine(vm = vm, host = host).take(originalState)
		assertTrue(transformed.vms.containsKey(vm.id))
		assertTrue(transformed.hosts.containsKey(host.id))
		assertNotNull(transformed.hosts[host.id]?.dynamic)
		assertNotNull(transformed.vms[vm.id]?.dynamic)
		assertEquals(VirtualMachineStatus.Up, transformed.vms[vm.id]?.dynamic?.status)
		assertEquals(host.id, transformed.vms[vm.id]?.dynamic?.hostId)
	}
}