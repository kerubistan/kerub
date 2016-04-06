package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.steps.host.powerdown.PowerDownHostFactory
import com.github.K0zka.kerub.planner.steps.host.startup.WakeHostFactory
import com.github.K0zka.kerub.planner.steps.vm.migrate.MigrateVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImageFactory
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateLvFactory
import com.github.K0zka.kerub.planner.steps.vstorage.migrate.MigrateVirtualStorageDeviceFactory
import com.github.K0zka.kerub.utils.join
import com.github.k0zka.finder4j.backtrack.StepFactory
import kotlin.comparisons.thenComparator
import kotlin.reflect.KClass

object CompositeStepFactory : StepFactory<AbstractOperationalStep, Plan> {

	val defaultFactories
			= setOf(MigrateVirtualMachineFactory, MigrateVirtualStorageDeviceFactory, PowerDownHostFactory,
			StartVirtualMachineFactory, StopVirtualMachineFactory)

	val factories = mapOf<KClass<*>, Set<AbstractOperationalStepFactory<*>>>(
			VirtualMachineAvailabilityExpectation::class
					to setOf(
					StartVirtualMachineFactory,
					CreateImageFactory,
					CreateLvFactory,
					StopVirtualMachineFactory,
					MigrateVirtualMachineFactory,
					WakeHostFactory
			),
			StorageAvailabilityExpectation::class to  setOf(
					CreateImageFactory,
					CreateLvFactory,
					WakeHostFactory,
					MigrateVirtualMachineFactory
			)
	)

	override fun produce(state: Plan): List<AbstractOperationalStep> {
		val stepFactories = state.state.getUnsatisfiedExpectations()
				.map {
					factories[it.javaClass.kotlin] ?: defaultFactories
				}.join().toSet()

		return sort(stepFactories.map { it.produce(state.state) }.join(), state)
	}

	fun sort(list: List<AbstractOperationalStep>, state: Plan): List<AbstractOperationalStep> {
		var list1 = list
		list1 = list1.sortedWith(StepBenefitComparator(state.state)
				.thenComparator { first, second -> StepCostComparator.compare(first, second) })
		return list1
	}

}