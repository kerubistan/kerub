package com.github.kerubistan.kerub.planner.steps.storage.migrate.live.libvirt

import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.migrate.AbstractMigrateVirtualStorageDevice
import kotlin.reflect.KClass

object LibvirtMigrateVirtualStorageDeviceFactory :
		AbstractOperationalStepFactory<AbstractMigrateVirtualStorageDevice>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf(NotSameStorageExpectation::class)

	override fun produce(state: OperationalState): List<AbstractMigrateVirtualStorageDevice> = TODO()
}