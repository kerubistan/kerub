package com.github.kerubistan.kerub.planner.issues.violations.plan

import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.expectations.NoMigrationExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachine
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import org.junit.Test
import java.util.UUID
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoMigrationExpectationViolationDetectorTest {
	@Test
	fun check() {
		assertTrue("blank state, no problem") {
			val expectation = NoMigrationExpectation(
					level = ExpectationLevel.DealBreaker,
					userTimeoutMinutes = 100
			)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			NoMigrationExpectationViolationDetector.check(
					vm,
					expectation,
					Plan(
							state = OperationalState.fromLists(
									vms = listOf(vm)
							),
							steps = listOf()
					)
			)
		}

		assertTrue("some other vm migrates, it is fine") {
			val expectation = NoMigrationExpectation(
					level = ExpectationLevel.DealBreaker,
					userTimeoutMinutes = 100
			)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			val otherVm = testVm.copy(
					id = UUID.randomUUID()
			)
			NoMigrationExpectationViolationDetector.check(
					vm,
					expectation,
					Plan(
							state = OperationalState.fromLists(
									vms = listOf(vm,otherVm),
									hosts = listOf(testHost, testFreeBsdHost)
							),
							steps = listOf(
									KvmMigrateVirtualMachine(otherVm, testHost, testFreeBsdHost)
							)
					)
			)

		}

		assertFalse("have a migration on the vm, detect problem") {
			val expectation = NoMigrationExpectation(
					level = ExpectationLevel.DealBreaker,
					userTimeoutMinutes = 100
			)
			val vm = testVm.copy(
					expectations = listOf(expectation)
			)
			NoMigrationExpectationViolationDetector.check(
					vm,
					expectation,
					Plan(
							state = OperationalState.fromLists(
									vms = listOf(vm),
									hosts = listOf(testHost, testFreeBsdHost)
							),
							steps = listOf(
								KvmMigrateVirtualMachine(vm, testHost, testFreeBsdHost)
							)
					)
			)
		}

	}

}