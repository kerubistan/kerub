package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.AbstractMigrateVirtualStorageDevice
import kotlin.reflect.KClass

object LibvirtMigrateVirtualStorageDeviceFactory :
		AbstractOperationalStepFactory<AbstractMigrateVirtualStorageDevice>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<AbstractMigrateVirtualStorageDevice>
			= TODO()
}