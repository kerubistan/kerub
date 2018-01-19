package com.github.kerubistan.kerub.planner.issues.violations.plan

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.NoMigrationExpectation
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.steps.vm.migrate.MigrateVirtualMachine
import com.github.kerubistan.kerub.planner.issues.violations.ViolationDetector

object NoMigrationExpectationViolationDetector :
		ViolationDetector<VirtualMachine, NoMigrationExpectation, Plan> {
	override fun check(entity: VirtualMachine, expectation: NoMigrationExpectation, state: Plan): Boolean =
			state.steps.none { it is MigrateVirtualMachine && it.vm.id == entity.id }
}