package com.github.kerubistan.kerub.planner.steps

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.model.expectations.NotSameStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanViolationDetector
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.steps.base.UnAllocateFactory
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHostFactory
import com.github.kerubistan.kerub.planner.steps.host.recycle.RecycleHostFactory
import com.github.kerubistan.kerub.planner.steps.host.security.HostSecurityCompositeFactory
import com.github.kerubistan.kerub.planner.steps.host.startup.WakeHostFactory
import com.github.kerubistan.kerub.planner.steps.vm.migrate.MigrateVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.start.StartVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vm.stop.StopVirtualMachineFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.CreateDiskFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.UnallocateDiskFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.lvm.duplicate.DuplicateToLvmFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt.LibvirtMigrateVirtualStorageDeviceFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.remove.RemoveVirtualStorageFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.ShareFactory
import com.github.kerubistan.kerub.utils.LogLevel
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.justToString
import com.github.kerubistan.kerub.utils.logAndReturn
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
			RecycleHostFactory, ShareFactory, HostSecurityCompositeFactory, DuplicateToLvmFactory, UnAllocateFactory,
			RemoveVirtualStorageFactory, UnallocateDiskFactory)

	private val factories = mapOf<KClass<*>, Set<AbstractOperationalStepFactory<*>>>(
			VirtualMachineAvailabilityExpectation::class
					to setOf(StartVirtualMachineFactory, CreateDiskFactory, UnallocateDiskFactory, StopVirtualMachineFactory,
					KvmMigrateVirtualMachineFactory, WakeHostFactory, ShareFactory, UnAllocateFactory),
			NotSameStorageExpectation::class to setOf(
					LibvirtMigrateVirtualStorageDeviceFactory, WakeHostFactory,
					MigrateVirtualMachineFactory),
			StorageAvailabilityExpectation::class to setOf(CreateDiskFactory, UnallocateDiskFactory, WakeHostFactory,
					MigrateVirtualMachineFactory)
	)

	private val problems =
			// all problem classes
			logger.logAndReturn(LogLevel.Info, "problem hints {}",
					defaultFactories.flatMap { it.problemHints }
							.map { problemClass ->
								problemClass to defaultFactories.filter { it.problemHints.contains(problemClass) }
							}.toMap(), ::justToString)

	override fun produce(state: Plan): List<AbstractOperationalStep> {
		val unsatisfiedExpectations = planViolationDetector.listViolations(state)
		logger.trace("unsatisfied expectations: {}", unsatisfiedExpectations)
		val stepFactories = unsatisfiedExpectations.values.join()
				.map {
					factories[it.javaClass.kotlin] ?: defaultFactories
				}.join().toSet()

		val planProblems = problemDetector.detect(state)
		logger.trace("problems {}", planProblems)
		val problemStepFactories = planProblems
				.map { it.javaClass.kotlin }
				.map { problems[it] ?: defaultFactories }.join().distinct()

		val steps = sort(list = (stepFactories + problemStepFactories).map { it.produce(state.state) }.join(),
				state = state)
		logger.trace("{} steps generated: {}", steps.size, steps)

		// let's not take a step that leads back to a previous planning phase, we could run endless rounds
		val filtered = filterSteps(steps, state)
		logger.trace("{} filtered steps: {}", filtered.size, filtered)

		return filtered
	}

	internal fun filterSteps(steps: List<AbstractOperationalStep>, plan: Plan) =
			steps.filterNot { step ->
				//the step leads to a state which we already had before (circle)
				plan.states.contains(step.take(plan.state))
						// there are similar steps before, e.g. resizing a pool after resizing the same pool
						// note a limitation here: we search for ANY similar step rather than a previous one
						// and that may need a rethink after a while, but this is already better than nothing
						|| plan.steps.any {
					previousStep ->
					previousStep is SimilarStep && previousStep.isLikeStep(step)
				} || plan.steps.contains(step)
			}

	internal fun sort(list: List<AbstractOperationalStep>,
					  detector: ProblemDetector<*> = CompositeProblemDetectorImpl,
					  state: Plan): List<AbstractOperationalStep> =
			list.sortedWith(StepBenefitComparator(planViolationDetector, state).reversed()
					.thenComparing(StepProblemsComparator(plan = state, detector = detector).reversed())
					.thenComparing(StepCostComparator).reversed())

}