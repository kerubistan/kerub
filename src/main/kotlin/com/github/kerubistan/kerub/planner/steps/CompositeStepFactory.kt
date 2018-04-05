package com.github.kerubistan.kerub.planner.steps

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanViolationDetector
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.issues.problems.hosts.RecyclingHost
import com.github.kerubistan.kerub.planner.issues.problems.hosts.UnusedService
import com.github.kerubistan.kerub.planner.issues.problems.vms.VmOnRecyclingHost
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHostFactory
import com.github.kerubistan.kerub.planner.steps.host.recycle.RecycleHostFactory
import com.github.kerubistan.kerub.planner.steps.host.startup.WakeHostFactory
import com.github.kerubistan.kerub.planner.steps.vm.migrate.MigrateVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.start.StartVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.stop.StopVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.CreateDiskFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt.LibvirtMigrateVirtualStorageDeviceFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.ShareFactory
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.join
import kotlin.reflect.KClass

class CompositeStepFactory(
		private val planViolationDetector: PlanViolationDetector,
		private val problemDetector: ProblemDetector<*> = CompositeProblemDetectorImpl
) :
		StepFactory<AbstractOperationalStep, Plan> {

	companion object {
		private val logger = getLogger(CompositeStepFactory::class)
	}

	private val defaultFactories = setOf(MigrateVirtualMachineFactory,
										 PowerDownHostFactory, StartVirtualMachineFactory, StopVirtualMachineFactory,
										 RecycleHostFactory, ShareFactory)

	private val factories = mapOf<KClass<*>, Set<AbstractOperationalStepFactory<*>>>(
			VirtualMachineAvailabilityExpectation::class
					to setOf(StartVirtualMachineFactory, CreateDiskFactory, StopVirtualMachineFactory,
							 KvmMigrateVirtualMachineFactory, WakeHostFactory, ShareFactory),
			NotSameStorageExpectation::class to setOf(
					LibvirtMigrateVirtualStorageDeviceFactory, WakeHostFactory,
					MigrateVirtualMachineFactory),
			StorageAvailabilityExpectation::class to setOf(CreateDiskFactory, WakeHostFactory,
														   MigrateVirtualMachineFactory)
	)

	private val problems = mapOf(
			RecyclingHost::class to setOf(MigrateVirtualMachineFactory, PowerDownHostFactory, RecycleHostFactory),
			UnusedService::class to setOf(),
			VmOnRecyclingHost::class to setOf()
	)

	override fun produce(state: Plan): List<AbstractOperationalStep> {
		val unsatisfiedExpectations = planViolationDetector.listViolations(state)
		logger.debug("unsatisfied expectations: {}", unsatisfiedExpectations)
		val stepFactories = unsatisfiedExpectations.values.join()
				.map {
					factories[it.javaClass.kotlin] ?: defaultFactories
				}.join().toSet()


		val planProblems = problemDetector.detect(state)
		logger.debug("problems {}", planProblems)
		val problemStepFactories = planProblems
				.map { it.javaClass.kotlin }
				.map { problems[it] ?: defaultFactories }.join().distinct()

		val steps = sort(list = (stepFactories + problemStepFactories).map { it.produce(state.state) }.join(),
						 state = state)
		logger.debug("{} steps generated: {}", steps.size, steps)

		// let's not take a step that leads back to a previous planning phase, we could run endless rounds
		val filtered = steps.filterNot { step -> state.states.contains(step.take(state.state)) }
		logger.debug("{} filtered steps: {}", filtered.size, filtered)

		return filtered
	}

	internal fun sort(list: List<AbstractOperationalStep>,
					  detector: ProblemDetector<*> = CompositeProblemDetectorImpl,
					  state: Plan): List<AbstractOperationalStep> =
			list.sortedWith(StepBenefitComparator(planViolationDetector, state).reversed()
									.thenComparing(StepProblemsComparator(plan = state, detector = detector).reversed())
									.thenComparing(StepCostComparator).reversed())

}