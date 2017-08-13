package com.github.K0zka.kerub.planner.execution

import com.github.K0zka.kerub.data.ExecutionResultDao
import com.github.K0zka.kerub.data.HostDao
import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.ControllerManager
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.ExecutionResult
import com.github.K0zka.kerub.model.StepExecutionError
import com.github.K0zka.kerub.model.StepExecutionPass
import com.github.K0zka.kerub.model.StepExecutionResult
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
import com.github.K0zka.kerub.planner.steps.host.recycle.RecycleHost
import com.github.K0zka.kerub.planner.steps.host.recycle.RecycleHostExecutor
import com.github.K0zka.kerub.planner.steps.host.startup.IpmiWakeHost
import com.github.K0zka.kerub.planner.steps.host.startup.WakeHostExecutor
import com.github.K0zka.kerub.planner.steps.host.startup.WolWakeHost
import com.github.K0zka.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vm.start.virtualbox.VirtualBoxStartVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.start.virtualbox.VirtualBoxStartVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.stop.StopVirtualMachineExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.fs.create.CreateImage
import com.github.K0zka.kerub.planner.steps.vstorage.fs.create.CreateImageExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create.CreateGvinumVolume
import com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create.CreateGvinumVolumeExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.lvm.create.CreateLv
import com.github.K0zka.kerub.planner.steps.vstorage.lvm.create.CreateLvExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.ctld.CtldIscsiShare
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.ctld.CtldIscsiShareExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd.TgtdIscsiShare
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd.TgtdIscsiShareExecutor
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.getStackTraceAsString
import nl.komponents.kovenant.task

class PlanExecutorImpl(
		private val executionResultDao: ExecutionResultDao,
		private val controllerManager: ControllerManager,
		hostCommandExecutor: HostCommandExecutor,
		hostManager: HostManager,
		hostDao: HostDao,
		hostDynamicDao: HostDynamicDao,
		vmDynamicDao: VirtualMachineDynamicDao,
		virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
		hostConfigurationDao: HostConfigurationDao
) : PlanExecutor {

	companion object {
		val logger = getLogger(PlanExecutorImpl::class)
	}

	val stepExecutors = mapOf<kotlin.reflect.KClass<*>, StepExecutor<*>>(
			KvmStartVirtualMachine::class to KvmStartVirtualMachineExecutor(hostManager, vmDynamicDao),
			VirtualBoxStartVirtualMachine::class to VirtualBoxStartVirtualMachineExecutor(hostCommandExecutor, vmDynamicDao),
			StopVirtualMachine::class to StopVirtualMachineExecutor(hostManager, vmDynamicDao),
			KvmMigrateVirtualMachine::class to KvmMigrateVirtualMachineExecutor(hostManager),
			EnableKsm::class to EnableKsmExecutor(hostCommandExecutor, hostDynamicDao),
			DisableKsm::class to DisableKsmExecutor(hostCommandExecutor, hostDynamicDao),
			CreateImage::class to CreateImageExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			CreateLv::class to CreateLvExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			CreateGvinumVolume::class to CreateGvinumVolumeExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao, hostDynamicDao),
			//TODO: handle both with just one (unless you want to maintain a long list)
			IpmiWakeHost::class to WakeHostExecutor(hostManager, hostDynamicDao),
			WolWakeHost::class to WakeHostExecutor(hostManager, hostDynamicDao),
			PowerDownHost::class to PowerDownExecutor(hostManager),
			TgtdIscsiShare::class to TgtdIscsiShareExecutor(hostConfigurationDao, hostCommandExecutor, hostManager),
			CtldIscsiShare::class to CtldIscsiShareExecutor(hostConfigurationDao, hostCommandExecutor, hostManager),
			RecycleHost::class to RecycleHostExecutor(hostDao, hostDynamicDao)
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
		val started = System.currentTimeMillis()
		//TODO: check synchronization need for this
		var stepOnExec: AbstractOperationalStep? = null
		var results = listOf<StepExecutionResult>()
		task {
			logger.debug("Executing plan {}", plan)
			for (step in plan.steps) {
				stepOnExec = step
				logger.debug("Executing step {}", step.javaClass.simpleName)
				execute(step)
				results += StepExecutionPass(executionStep = step)
			}
		} fail {
			exc ->
			logger.warn("plan execution failed", exc)
			stepOnExec?.let {
				results += StepExecutionError(error = exc.getStackTraceAsString(), executionStep = it)
			}
		} always {
			logger.debug("Plan execution finished: {}", plan)
			synchronized(results) {
				executionResultDao.add(
						ExecutionResult(
								started = started,
								controllerId = controllerManager.getControllerId(),
								steps = results
						)
				)
			}
			callback(plan)
		}
	}
}