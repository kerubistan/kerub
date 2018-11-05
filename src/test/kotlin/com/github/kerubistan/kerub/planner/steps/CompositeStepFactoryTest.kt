package com.github.kerubistan.kerub.planner.steps

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanViolationDetector
import com.github.kerubistan.kerub.planner.PlanViolationDetectorImpl
import com.github.kerubistan.kerub.planner.execution.PlanExecutorImpl
import com.github.kerubistan.kerub.planner.issues.problems.ProblemDetector
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon.StartNfsDaemon
import com.github.kerubistan.kerub.planner.steps.vstorage.share.nfs.daemon.StopNfsDaemon
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testVm
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import kotlin.test.assertTrue

class CompositeStepFactoryTest {

	@Test
	fun validateStepsHaveExecutors() {
		// all steps produced by the step factories registered in CompositeStepFactory
		// must have an executor registered in PlanExecutorImpl

		val planExecutor = PlanExecutorImpl(mock(), mock(), mock(), mock(), mock(), mock(), mock(), mock(), mock(),
				mock())

		CompositeStepFactory.defaultFactories.forEach { factory ->
			validateStepFactory(factory, planExecutor)
		}

	}

	private fun validateStepFactory(factory: AbstractOperationalStepFactory<out AbstractOperationalStep>, planExecutor: PlanExecutorImpl) {
		when (factory) {
			is StepFactoryCollection -> {
				factory.factories.forEach {
					validateStepFactory(it, planExecutor)
				}
			}
			else -> {
				val stepType = Class.forName(factory.javaClass.kotlin.supertypes[0].arguments[0].type.toString()).kotlin
				if(!stepType.isAbstract) {
					assertTrue("plan executor must handle $stepType because $factory produces it") {
						planExecutor.stepExecutors.keys.contains(stepType)
					}
				} else {
					//TODO get the subtypes, check them all
				}
			}
		}
	}

	@Test
	fun filterSteps() {
		assertTrue("filter out steps that had been executed and been inverted already") {
			val steps = listOf(
					StartNfsDaemon(host = testHost),
					StopNfsDaemon(host = testHost)
			)
			CompositeStepFactory(mock(), mock()).filterSteps(
					listOf(
							StartNfsDaemon(host = testHost)
					),
					Plan.planBy(
							OperationalState.fromLists(
									hosts = listOf(testHost)
							),
							steps
					)
			).isEmpty()
		}
	}

	@Test
	fun produce() {
		val vm = VirtualMachine(
				name = "test-vm",
				expectations = listOf(VirtualMachineAvailabilityExpectation(up = true))
		)
		val plan = Plan(
				state = OperationalState.fromLists(
						vms = listOf(vm),
						hostDyns = listOf()
				),
				steps = listOf()
		)
		val steps = CompositeStepFactory(PlanViolationDetectorImpl).produce(plan)

		//TODO:
	}

	@Test
	fun produceFromEmpty() {
		val plan = Plan(
				state = OperationalState.fromLists(),
				steps = listOf()
		)
		val steps = CompositeStepFactory(PlanViolationDetectorImpl).produce(plan)

		assertTrue { steps.isEmpty() }
	}

	@Test
	fun sort() {
		assertTrue("two steps, the first creates violations") {
			val violationDetector = mock<PlanViolationDetector>()
			val step1 = mock<AbstractOperationalStep>()
			val step2 = mock<AbstractOperationalStep>()

			val plan = Plan(state = OperationalState())
			val step1TargetState = Plan(state = OperationalState(), steps = listOf(step1))
			val step2TargetState = Plan(state = OperationalState(), steps = listOf(step2))
			val detector = mock<ProblemDetector<*>>()

			val dealBreaker = mock<Expectation>()
			whenever(dealBreaker.level).thenReturn(ExpectationLevel.DealBreaker)

			val wish = mock<Expectation>()
			whenever(wish.level).thenReturn(ExpectationLevel.Wish)

			whenever(step1.take(eq(plan))).thenReturn(step1TargetState)
			whenever(step2.take(eq(plan))).thenReturn(step2TargetState)

			whenever(violationDetector.listViolations(eq(step1TargetState))).thenReturn(
					mapOf(testVm to listOf(dealBreaker, wish)))

			whenever(violationDetector.listViolations(eq(step2TargetState))).thenReturn(mapOf())

			val ordered = CompositeStepFactory(violationDetector).sort(
					list = listOf(step1, step2),
					detector = detector,
					state = plan
			)

			ordered == listOf(step2, step1)

		}

		assertTrue("two steps, both create 1 violations, only the first creates a problem") {
			val violationDetector = mock<PlanViolationDetector>()
			val step1 = mock<AbstractOperationalStep>()
			val step2 = mock<AbstractOperationalStep>()

			val plan = Plan(state = OperationalState())
			val step1TargetState = Plan(state = OperationalState(), steps = listOf(step1))
			val step2TargetState = Plan(state = OperationalState(), steps = listOf(step2))
			val detector = mock<ProblemDetector<*>>()

			whenever(step1.take(eq(plan))).thenReturn(step1TargetState)
			whenever(step2.take(eq(plan))).thenReturn(step2TargetState)

			whenever(violationDetector.listViolations(step1TargetState)).thenReturn(
					mapOf(testVm to listOf(mock())))

			whenever(violationDetector.listViolations(step1TargetState)).thenReturn(
					mapOf(testVm to listOf(mock()))
			)

			whenever(detector.detect(eq(step1TargetState))).thenReturn(listOf(mock()))
			whenever(detector.detect(eq(step2TargetState))).thenReturn(listOf())

			val ordered = CompositeStepFactory(violationDetector).sort(
					list = listOf(step1, step2),
					detector = detector,
					state = plan
			)

			ordered == listOf(step2, step1)
		}

		assertTrue("two steps, first with deal-breaker, second with wish") {
			val violationDetector = mock<PlanViolationDetector>()
			val step1 = mock<AbstractOperationalStep>()
			val step2 = mock<AbstractOperationalStep>()

			val plan = Plan(state = OperationalState())
			val step1TargetState = Plan(state = OperationalState(), steps = listOf(step1))
			val step2TargetState = Plan(state = OperationalState(), steps = listOf(step2))
			val detector = mock<ProblemDetector<*>>()

			val dealBreaker = mock<Expectation>()
			whenever(dealBreaker.level).thenReturn(ExpectationLevel.DealBreaker)

			val wish = mock<Expectation>()
			whenever(wish.level).thenReturn(ExpectationLevel.Wish)

			whenever(step1.take(eq(plan))).thenReturn(step1TargetState)
			whenever(step2.take(eq(plan))).thenReturn(step2TargetState)

			whenever(violationDetector.listViolations(eq(step1TargetState))).thenReturn(
					mapOf(testVm to listOf(dealBreaker)))

			whenever(violationDetector.listViolations(eq(step2TargetState))).thenReturn(
					mapOf(testVm to listOf(wish)))

			val ordered = CompositeStepFactory(violationDetector).sort(
					list = listOf(step1, step2),
					detector = detector,
					state = plan
			)

			ordered == listOf(step2, step1)
		}

	}
}