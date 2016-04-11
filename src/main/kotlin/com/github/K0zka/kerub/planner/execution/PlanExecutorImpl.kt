package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.PlanExecutor
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.planner.steps.host.ksm.DisableKsm
import com.github.K0zka.kerub.planner.steps.host.ksm.DisableKsmExecutor
import com.github.K0zka.kerub.planner.steps.host.ksm.EnableKsm
import com.github.K0zka.kerub.planner.steps.host.ksm.EnableKsmExecutor
import com.github.K0zka.kerub.planner.steps.host.powerdown.PowerDownExecutor
import com.github.K0zka.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.K0zka.kerub.planner.steps.host.startup.WakeHost
import com.github.K0zka.kerub.planner.steps.host.startup.WakeHostExecutor
import com.github.K0zka.kerub.planner.steps.vm.migrate.MigrateVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.migrate.MigrateVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImage
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImageExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateLv
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateLvExecutor
import com.github.K0zka.kerub.utils.getLogger
import nl.komponents.kovenant.task

class PlanExecutorImpl(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostManager: HostManager,
		private val hostDynamicDao: HostDynamicDao,
		private val vmDynamicDao: VirtualMachineDynamicDao,
		private val virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao
) : PlanExecutor {

	companion object {
		val logger = getLogger(PlanExecutorImpl::class)
	}

	val stepExecutors = mapOf<kotlin.reflect.KClass<*>, StepExecutor<*>>(
			StartVirtualMachine::class to StartVirtualMachineExecutor(hostManager, vmDynamicDao),
			StopVirtualMachine::class to StopVirtualMachineExecutor(hostManager, vmDynamicDao),
			MigrateVirtualMachine::class to MigrateVirtualMachineExecutor(hostManager),
			EnableKsm::class to EnableKsmExecutor(hostCommandExecutor, hostDynamicDao),
			DisableKsm::class to DisableKsmExecutor(hostCommandExecutor, hostDynamicDao),
			CreateImage::class to CreateImageExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			CreateLv::class to CreateLvExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			WakeHost::class to WakeHostExecutor(hostManager, hostDynamicDao),
			PowerDownHost::class to PowerDownExecutor(hostManager)
	)

	fun execute(step: AbstractOperationalStep) {
		val executor = stepExecutors.get(step.javaClass.kotlin)
		if (executor == null) {
			throw IllegalArgumentException("No executor for step ${step}")
		} else {
			(executor as StepExecutor<AbstractOperationalStep>).execute(step)
		}
	}

	override fun
			execute(plan: Plan, callback: (Plan) -> Unit) {
		task() {
			logger.debug("Executing plan {}", plan)
			for (step in plan.steps) {
				logger.debug("Executing step {}", step.javaClass.simpleName)
				execute(step)
			}
		} always {
			logger.debug("Plan execution finished: {}", plan)
			callback(plan)
		} fail {
			logger.warn("plan execution failed", it)
		}
	}
}