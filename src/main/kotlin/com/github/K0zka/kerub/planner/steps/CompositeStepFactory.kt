package com.github.K0zka.kerub.planner.steps

import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.steps.host.powerdown.PowerDownHostFactory
import com.github.K0zka.kerub.planner.steps.host.startup.WakeHostFactory
import com.github.K0zka.kerub.planner.steps.vm.migrate.MigrateVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachineFactory
import com.github.K0zka.kerub.planner.steps.vstorage.migrate.MigrateVirtualStorageDeviceFactory
import com.github.k0zka.finder4j.backtrack.StepFactory
import kotlin.reflect.KClass

public object CompositeStepFactory : StepFactory<AbstractOperationalStep, Plan> {

	val defaultFactories
			= setOf(MigrateVirtualMachineFactory, MigrateVirtualStorageDeviceFactory, PowerDownHostFactory,
			         StartVirtualMachineFactory, StopVirtualMachineFactory)

	val factories = mapOf<KClass<*>, Set<AbstractOperationalStepFactory<*>>>(
			VirtualMachineAvailabilityExpectation::class
					to setOf<AbstractOperationalStepFactory<*>>(
						StartVirtualMachineFactory,
						StopVirtualMachineFactory,
					    MigrateVirtualMachineFactory,
					    WakeHostFactory
					         )
	                                                                                   )

	override fun produce(state: Plan): List<AbstractOperationalStep> {

		val unsat = state.state.getUnsatisfiedExpectations()
		unsat.map { it.javaClass }.toSet()

		var allStepFactories = setOf<AbstractOperationalStepFactory<*>>()
		unsat.forEach {
			allStepFactories = (allStepFactories + (factories[it.javaClass.kotlin] ?: defaultFactories)).toSet()
		}

		var list = listOf<AbstractOperationalStep>()
		allStepFactories.forEach { list += (it.produce(state.state) )}
		list = list.sortedWith( StepBenefitComparator(state.state)
				                    .thenComparator { first, second -> StepCostComparator.reversed().compare(first, second) } )
		return list
	}

}