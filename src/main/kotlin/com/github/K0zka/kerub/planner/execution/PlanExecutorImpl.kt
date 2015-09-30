package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.PlanExecutor
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.host.ksm.*
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImage
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImageExecutor
import com.github.K0zka.kerub.utils.getLogger
import com.github.k0zka.finder4j.backtrack.Step
import kotlin.reflect.jvm.kotlin

public class PlanExecutorImpl(val hostCommandExecutor : HostCommandExecutor) : PlanExecutor {

	companion object {
		val logger = getLogger(PlanExecutorImpl::class)
	}

	val stepExecutors = mapOf<kotlin.reflect.KClass<*>, StepExecutor<*>>(
			EnableKsm::class to EnableKsmExecutor(hostCommandExecutor),
	        DisableKsm::class to DisableKsmExecutor(hostCommandExecutor),
	        CreateImage::class to CreateImageExecutor(hostCommandExecutor)
	                         )

	fun execute(step : AbstractOperationalStep) {
		val executor = stepExecutors.get(step.javaClass.kotlin)
		if(executor == null) {
			throw IllegalArgumentException("No executor for step ${step}")
		} else {
			(executor as StepExecutor<AbstractOperationalStep>).execute(step)
		}
	}

	override fun execute(plan: Plan) {
		logger.debug("Executing plan {}", plan)
		for(step in plan.steps) {
			execute(step)
		}
		logger.debug("Plan execution finished: {}", plan)
	}
}