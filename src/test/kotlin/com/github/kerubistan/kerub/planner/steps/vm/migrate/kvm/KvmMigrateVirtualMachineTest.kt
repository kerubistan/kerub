package com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm

import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue

class KvmMigrateVirtualMachineTest : OperationalStepVerifications() {
	override val step: KvmMigrateVirtualMachine
		get() {
			val srcHost = testHost.copy(id = UUID.randomUUID())
			val dstHost = testHost.copy(id = UUID.randomUUID())

			return KvmMigrateVirtualMachine(vm = testVm, source = srcHost, target = dstHost)
		}

	@Test
	fun isInverseOf() {
		assertTrue("inverse migration") {
			val srcHost = testHost.copy(id = UUID.randomUUID())
			val dstHost = testHost.copy(id = UUID.randomUUID())
			KvmMigrateVirtualMachine(vm = testVm, source = srcHost, target = dstHost)
					.isInverseOf(KvmMigrateVirtualMachine(vm = testVm, source = dstHost, target = srcHost))
		}
		assertTrue("not inverse migration (vm is different)") {
			val otheVm = testVm.copy(id = UUID.randomUUID())
			val srcHost = testHost.copy(id = UUID.randomUUID())
			val dstHost = testHost.copy(id = UUID.randomUUID())
			!KvmMigrateVirtualMachine(vm = testVm, source = srcHost, target = dstHost)
					.isInverseOf(KvmMigrateVirtualMachine(vm = otheVm, source = dstHost, target = srcHost))
		}
	}

	@Test
	fun getCost() {
		val srcHost = testHost.copy(id = UUID.randomUUID())
		val dstHost = testHost.copy(id = UUID.randomUUID())
		val costs = KvmMigrateVirtualMachine(vm = testVm, source = srcHost, target = dstHost).getCost()

		assertTrue { costs.isNotEmpty() }
	}
}