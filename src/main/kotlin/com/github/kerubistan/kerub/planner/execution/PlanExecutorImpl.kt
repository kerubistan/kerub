package com.github.kerubistan.kerub.planner.execution

import com.github.kerubistan.kerub.data.ExecutionResultDao
import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.ControllerManager
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.model.ExecutionResult
import com.github.kerubistan.kerub.model.StepExecutionError
import com.github.kerubistan.kerub.model.StepExecutionPass
import com.github.kerubistan.kerub.model.StepExecutionResult
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanExecutor
import com.github.kerubistan.kerub.planner.StepExecutor
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.host.ksm.DisableKsm
import com.github.kerubistan.kerub.planner.steps.host.ksm.DisableKsmExecutor
import com.github.kerubistan.kerub.planner.steps.host.ksm.EnableKsm
import com.github.kerubistan.kerub.planner.steps.host.ksm.EnableKsmExecutor
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownExecutor
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.host.recycle.RecycleHost
import com.github.kerubistan.kerub.planner.steps.host.recycle.RecycleHostExecutor
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKey
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKeyExecutor
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKey
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKeyExecutor
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKey
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKeyExecutor
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKey
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKeyExecutor
import com.github.kerubistan.kerub.planner.steps.host.startup.IpmiWakeHost
import com.github.kerubistan.kerub.planner.steps.host.startup.WakeHostExecutor
import com.github.kerubistan.kerub.planner.steps.host.startup.WolWakeHost
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageExecutor
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImageExecutor
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFs
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFsExecutor
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolume
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolumeExecutor
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinum
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinumExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLvExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLvExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.duplicate.DuplicateToLvm
import com.github.kerubistan.kerub.planner.steps.storage.lvm.duplicate.DuplicateToLvmExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror.MirrorVolume
import com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror.MirrorVolumeExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create.CreateLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create.CreateLvmPoolExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend.ExtendLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend.ExtendLvmPoolExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove.RemoveLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove.RemoveLvmPoolExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLvExecutor
import com.github.kerubistan.kerub.planner.steps.storage.lvm.vg.RemoveDiskFromVG
import com.github.kerubistan.kerub.planner.steps.storage.lvm.vg.RemoveDiskFromVGExecutor
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block.MigrateBlockAllocation
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block.MigrateBlockAllocationExecutor
import com.github.kerubistan.kerub.planner.steps.storage.mount.MountNfs
import com.github.kerubistan.kerub.planner.steps.storage.mount.MountNfsExecutor
import com.github.kerubistan.kerub.planner.steps.storage.mount.UnmountNfs
import com.github.kerubistan.kerub.planner.steps.storage.mount.UnmountNfsExecutor
import com.github.kerubistan.kerub.planner.steps.storage.remove.RemoveVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.remove.RemoveVirtualStorageExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld.CtldIscsiShare
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld.CtldIscsiShareExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiShare
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiShareExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.ShareNfs
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.ShareNfsExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.UnshareNfs
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.UnshareNfsExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StartNfsDaemon
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StartNfsDaemonExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StopNfsDaemon
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StopNfsDaemonExecutor
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachineExecutor
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachineExecutor
import com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox.VirtualBoxStartVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox.VirtualBoxStartVirtualMachineExecutor
import com.github.kerubistan.kerub.planner.steps.vm.stop.StopVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.stop.StopVirtualMachineExecutor
import com.github.kerubistan.kerub.utils.getLogger
import com.github.kerubistan.kerub.utils.getStackTraceAsString
import com.github.kerubistan.kerub.utils.now
import nl.komponents.kovenant.task

class PlanExecutorImpl(
		private val executionResultDao: ExecutionResultDao,
		private val controllerManager: ControllerManager,
		hostCommandExecutor: HostCommandExecutor,
		hostManager: HostManager,
		hostDao: HostDao,
		hostDynamicDao: HostDynamicDao,
		vmDynamicDao: VirtualMachineDynamicDao,
		vssDao: VirtualStorageDeviceDao,
		virtualStorageDeviceDynamicDao: VirtualStorageDeviceDynamicDao,
		hostConfigurationDao: HostConfigurationDao
) : PlanExecutor {

	companion object {
		private val logger = getLogger(PlanExecutorImpl::class)
	}

	@ExperimentalUnsignedTypes
	val stepExecutors = mapOf<kotlin.reflect.KClass<*>, StepExecutor<*>>(
			KvmStartVirtualMachine::class to KvmStartVirtualMachineExecutor(
					hostManager,
					vmDynamicDao,
					hostCommandExecutor),
			VirtualBoxStartVirtualMachine::class to VirtualBoxStartVirtualMachineExecutor(
					hostCommandExecutor,
					vmDynamicDao),
			StopVirtualMachine::class to StopVirtualMachineExecutor(hostManager, vmDynamicDao),
			KvmMigrateVirtualMachine::class to KvmMigrateVirtualMachineExecutor(hostManager),
			EnableKsm::class to EnableKsmExecutor(hostCommandExecutor, hostDynamicDao),
			DisableKsm::class to DisableKsmExecutor(hostCommandExecutor, hostDynamicDao),
			CreateImage::class to CreateImageExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			TruncateImage::class to TruncateImageExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			CreateGvinumVolume::class to CreateGvinumVolumeExecutor(
					hostCommandExecutor, virtualStorageDeviceDynamicDao,
					hostDynamicDao),

			IpmiWakeHost::class to WakeHostExecutor(hostManager, hostDynamicDao),
			WolWakeHost::class to WakeHostExecutor(hostManager, hostDynamicDao),
			PowerDownHost::class to PowerDownExecutor(hostManager),
			TgtdIscsiShare::class to TgtdIscsiShareExecutor(hostConfigurationDao, hostCommandExecutor, hostManager),
			CtldIscsiShare::class to CtldIscsiShareExecutor(hostConfigurationDao, hostCommandExecutor, hostManager),
			RecycleHost::class to RecycleHostExecutor(hostDao, hostDynamicDao),

			//LVM
			CreateLv::class to CreateLvExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			CreateLvmPool::class to CreateLvmPoolExecutor(hostCommandExecutor, hostConfigurationDao),
			ExtendLvmPool::class to ExtendLvmPoolExecutor(hostCommandExecutor, hostConfigurationDao, hostDynamicDao),
			UnAllocateLv::class to UnAllocateLvExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			DuplicateToLvm::class to DuplicateToLvmExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			CreateThinLv::class to CreateThinLvExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			//ShrinkLvmPool::class to ShrinkLvmPoolExecutor(hostCommandExecutor, hostConfigurationDao),
			RemoveLvmPool::class to RemoveLvmPoolExecutor(hostCommandExecutor, hostConfigurationDao, hostDynamicDao),
			RemoveDiskFromVG::class to RemoveDiskFromVGExecutor(hostCommandExecutor, hostDao),
			MirrorVolume::class to MirrorVolumeExecutor(
					hostCommandExecutor,
					hostDynamicDao,
					virtualStorageDeviceDynamicDao),

			//NFS
			StartNfsDaemon::class to StartNfsDaemonExecutor(hostManager, hostConfigurationDao),
			StopNfsDaemon::class to StopNfsDaemonExecutor(hostManager, hostConfigurationDao),
			ShareNfs::class to ShareNfsExecutor(hostConfigurationDao, hostCommandExecutor),
			UnshareNfs::class to UnshareNfsExecutor(hostConfigurationDao, hostCommandExecutor),
			MountNfs::class to MountNfsExecutor(hostCommandExecutor, hostConfigurationDao),
			UnmountNfs::class to UnmountNfsExecutor(hostCommandExecutor, hostConfigurationDao),

			//storage un-allocation
			UnAllocateGvinum::class to UnAllocateGvinumExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			UnAllocateFs::class to UnAllocateFsExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),
			UnAllocateLv::class to UnAllocateLvExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao),

			RemoveVirtualStorage::class to RemoveVirtualStorageExecutor(vssDao, virtualStorageDeviceDynamicDao),

			// SSH
			ClearSshKey::class to ClearSshKeyExecutor(hostCommandExecutor, hostConfigurationDao),
			GenerateSshKey::class to GenerateSshKeyExecutor(hostCommandExecutor, hostConfigurationDao),
			InstallPublicKey::class to InstallPublicKeyExecutor(hostCommandExecutor, hostConfigurationDao),
			RemovePublicKey::class to RemovePublicKeyExecutor(hostCommandExecutor, hostConfigurationDao),

			MigrateBlockAllocation::class to MigrateBlockAllocationExecutor(hostCommandExecutor, virtualStorageDeviceDynamicDao, hostDynamicDao)

	)

	fun execute(step: AbstractOperationalStep) {
		val executor = stepExecutors[step.javaClass.kotlin]
		if (executor == null) {
			throw IllegalArgumentException("No executor for step $step")
		} else {
			(executor as StepExecutor<AbstractOperationalStep>).execute(step)
		}
	}

	override fun
			execute(plan: Plan, callback: (Plan) -> Unit) {
		val started = now()
		//TODO: check synchronization need for this
		var stepOnExec: AbstractOperationalStep? = null
		val results = mutableListOf<StepExecutionResult>()
		val stepList = plan.steps.map { it.javaClass.simpleName }
		task {
			logger.debug("Executing plan {}", stepList)
			for (step in plan.steps) {
				stepOnExec = step
				logger.debug("Executing step {}", step.javaClass.simpleName)
				execute(step)
				results += StepExecutionPass(executionStep = step)
			}
		} fail { exc ->
			logger.warn("plan execution failed: {}\nat step:{}\n{}", stepList, stepOnExec?.javaClass?.simpleName, plan, exc)
			stepOnExec?.let {
				results += StepExecutionError(error = exc.getStackTraceAsString(), executionStep = it)
			}
		} always {
			logger.debug("Plan execution finished: {}", stepList)
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