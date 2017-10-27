package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHostFactory
import com.github.kerubistan.kerub.planner.steps.host.recycle.RecycleHostFactory
import com.github.kerubistan.kerub.planner.steps.host.startup.WakeHostFactory
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.start.StartVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.stop.StopVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.CreateDiskFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.MigrateVirtualStorageDeviceFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.IscsiShareFactory
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.join
import com.github.k0zka.finder4j.backtrack.StepFactory
import kotlin.reflect.KClass

object CompositeStepFactory : StepFactory<AbstractOperationalStep, Plan> {

	val logger = getLogger(CompositeStepFactory::class)

	val defaultFactories
			= setOf(KvmMigrateVirtualMachineFactory, MigrateVirtualStorageDeviceFactory, PowerDownHostFactory,
			StartVirtualMachineFactory, StopVirtualMachineFactory, RecycleHostFactory)

	val factories = mapOf<KClass<*>, Set<AbstractOperationalStepFactory<*>>>(
			VirtualMachineAvailabilityExpectation::class
					to setOf(
					StartVirtualMachineFactory,
					CreateDiskFactory,
					StopVirtualMachineFactory,
					KvmMigrateVirtualMachineFactory,
					WakeHostFactory,
					IscsiShareFactory
			),
			NotSameStorageExpectation::class to setOf(
					MigrateVirtualStorageDeviceFactory,
					WakeHostFactory,
					KvmMigrateVirtualMachineFactory
			),
			StorageAvailabilityExpectation::class to setOf(
					CreateDiskFactory,
					WakeHostFactory,
					KvmMigrateVirtualMachineFactory
			)
	)

	override fun produce(state: Plan): List<AbstractOperationalStep> {
		val unsatisfiedExpectations = state.state.getUnsatisfiedExpectations()
		logger.debug("unsatisfied expectations: {}", unsatisfiedExpectations)
		val stepFactories = unsatisfiedExpectations
				.map {
					factories[it.javaClass.kotlin] ?: defaultFactories
				}.join().toSet()

		val steps = sort(stepFactories.map { it.produce(state.state) }.join(), state)
		logger.debug("steps generated: {}", steps)
		return steps
	}

	fun sort(list: List<AbstractOperationalStep>, state: Plan): List<AbstractOperationalStep> {
		var list1 = list
		list1 = list1.sortedWith(StepBenefitComparator(state.state)
				.thenComparator { first, second -> StepCostComparator.compare(first, second) })
		return list1
	}

}