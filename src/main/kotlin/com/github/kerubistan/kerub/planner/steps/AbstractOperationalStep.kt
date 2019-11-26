package com.github.kerubistan.kerub.planner.steps

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.k0zka.finder4j.backtrack.Step
import com.github.kerubistan.kerub.model.ExecutionStep
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.reservations.Reservation
import com.github.kerubistan.kerub.planner.steps.host.fence.FenceHost
import com.github.kerubistan.kerub.planner.steps.host.install.InstallSoftware
import com.github.kerubistan.kerub.planner.steps.host.ksm.DisableKsm
import com.github.kerubistan.kerub.planner.steps.host.ksm.EnableKsm
import com.github.kerubistan.kerub.planner.steps.host.ksm.TuneKsm
import com.github.kerubistan.kerub.planner.steps.host.powerdown.PowerDownHost
import com.github.kerubistan.kerub.planner.steps.host.recycle.RecycleHost
import com.github.kerubistan.kerub.planner.steps.host.security.clear.ClearSshKey
import com.github.kerubistan.kerub.planner.steps.host.security.generate.GenerateSshKey
import com.github.kerubistan.kerub.planner.steps.host.security.install.InstallPublicKey
import com.github.kerubistan.kerub.planner.steps.host.security.remove.RemovePublicKey
import com.github.kerubistan.kerub.planner.steps.host.startup.IpmiWakeHost
import com.github.kerubistan.kerub.planner.steps.host.startup.WolWakeHost
import com.github.kerubistan.kerub.planner.steps.network.ovs.port.create.CreateOvsPort
import com.github.kerubistan.kerub.planner.steps.network.ovs.port.gre.CreateOvsGrePort
import com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove.RemoveOvsPort
import com.github.kerubistan.kerub.planner.steps.network.ovs.sw.create.CreateOvsSwitch
import com.github.kerubistan.kerub.planner.steps.network.ovs.sw.remove.RemoveOvsSwitch
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.local.LocalBlockCopy
import com.github.kerubistan.kerub.planner.steps.storage.block.copy.remote.RemoteBlockCopy
import com.github.kerubistan.kerub.planner.steps.storage.fs.convert.inplace.InPlaceConvertImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.convert.othercap.ConvertImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.create.CreateImageBasedOnTemplate
import com.github.kerubistan.kerub.planner.steps.storage.fs.fallocate.FallocateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.rebase.RebaseImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.truncate.TruncateImage
import com.github.kerubistan.kerub.planner.steps.storage.fs.unallocate.UnAllocateFs
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.create.CreateGvinumVolume
import com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate.UnAllocateGvinum
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateThinLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.duplicate.DuplicateToLvm
import com.github.kerubistan.kerub.planner.steps.storage.lvm.mirror.MirrorVolume
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create.CreateLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.extend.ExtendLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.remove.RemoveLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.lvm.unallocate.UnAllocateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.vg.RemoveDiskFromVG
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.block.MigrateBlockAllocation
import com.github.kerubistan.kerub.planner.steps.storage.migrate.dead.file.MigrateFileAllocation
import com.github.kerubistan.kerub.planner.steps.storage.migrate.live.libvirt.LibvirtMigrateVirtualStorageDevice
import com.github.kerubistan.kerub.planner.steps.storage.mount.MountNfs
import com.github.kerubistan.kerub.planner.steps.storage.mount.UnmountNfs
import com.github.kerubistan.kerub.planner.steps.storage.remove.RemoveVirtualStorage
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld.CtldIscsiShare
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiShare
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiUnshare
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.ShareNfs
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.UnshareNfs
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StartNfsDaemon
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StopNfsDaemon
import com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity.ClearCpuAffinity
import com.github.kerubistan.kerub.planner.steps.vm.cpuaffinity.SetCpuAffinity
import com.github.kerubistan.kerub.planner.steps.vm.migrate.kvm.KvmMigrateVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.pause.PauseVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.resume.ResumeVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.start.virtualbox.VirtualBoxStartVirtualMachine
import com.github.kerubistan.kerub.planner.steps.vm.stop.StopVirtualMachine
import kotlin.reflect.KClass

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(
		JsonSubTypes.Type(ClearCpuAffinity::class),
		JsonSubTypes.Type(ClearSshKey::class),
		JsonSubTypes.Type(ConvertImage::class),
		JsonSubTypes.Type(CreateGvinumVolume::class),
		JsonSubTypes.Type(CreateImage::class),
		JsonSubTypes.Type(FenceHost::class),
		JsonSubTypes.Type(InstallSoftware::class),
		JsonSubTypes.Type(DisableKsm::class),
		JsonSubTypes.Type(EnableKsm::class),
		JsonSubTypes.Type(TuneKsm::class),
		JsonSubTypes.Type(PowerDownHost::class),
		JsonSubTypes.Type(RecycleHost::class),
		JsonSubTypes.Type(GenerateSshKey::class),
		JsonSubTypes.Type(InstallPublicKey::class),
		JsonSubTypes.Type(RemovePublicKey::class),
		JsonSubTypes.Type(IpmiWakeHost::class),
		JsonSubTypes.Type(WolWakeHost::class),
		JsonSubTypes.Type(LocalBlockCopy::class),
		JsonSubTypes.Type(RemoteBlockCopy::class),
		JsonSubTypes.Type(InPlaceConvertImage::class),
		JsonSubTypes.Type(CreateImageBasedOnTemplate::class),
		JsonSubTypes.Type(FallocateImage::class),
		JsonSubTypes.Type(RebaseImage::class),
		JsonSubTypes.Type(TruncateImage::class),
		JsonSubTypes.Type(UnAllocateFs::class),
		JsonSubTypes.Type(UnAllocateGvinum::class),
		JsonSubTypes.Type(CreateLv::class),
		JsonSubTypes.Type(CreateThinLv::class),
		JsonSubTypes.Type(DuplicateToLvm::class),
		JsonSubTypes.Type(MirrorVolume::class),
		JsonSubTypes.Type(CreateLvmPool::class),
		JsonSubTypes.Type(ExtendLvmPool::class),
		JsonSubTypes.Type(RemoveLvmPool::class),
		JsonSubTypes.Type(UnAllocateLv::class),
		JsonSubTypes.Type(RemoveDiskFromVG::class),
		JsonSubTypes.Type(MigrateBlockAllocation::class),
		JsonSubTypes.Type(MigrateFileAllocation::class),
		JsonSubTypes.Type(LibvirtMigrateVirtualStorageDevice::class),
		JsonSubTypes.Type(MountNfs::class),
		JsonSubTypes.Type(UnmountNfs::class),
		JsonSubTypes.Type(RemoveVirtualStorage::class),
		JsonSubTypes.Type(CtldIscsiShare::class),
		JsonSubTypes.Type(TgtdIscsiShare::class),
		JsonSubTypes.Type(TgtdIscsiUnshare::class),
		JsonSubTypes.Type(ShareNfs::class),
		JsonSubTypes.Type(UnshareNfs::class),
		JsonSubTypes.Type(StartNfsDaemon::class),
		JsonSubTypes.Type(StopNfsDaemon::class),
		JsonSubTypes.Type(SetCpuAffinity::class),
		JsonSubTypes.Type(KvmMigrateVirtualMachine::class),
		JsonSubTypes.Type(PauseVirtualMachine::class),
		JsonSubTypes.Type(ResumeVirtualMachine::class),
		JsonSubTypes.Type(KvmStartVirtualMachine::class),
		JsonSubTypes.Type(VirtualBoxStartVirtualMachine::class),
		JsonSubTypes.Type(StopVirtualMachine::class),
		JsonSubTypes.Type(CreateOvsSwitch::class),
		JsonSubTypes.Type(RemoveOvsSwitch::class),
		JsonSubTypes.Type(CreateOvsPort::class),
		JsonSubTypes.Type(RemoveOvsPort::class),
		JsonSubTypes.Type(CreateOvsGrePort::class)
)
interface AbstractOperationalStep : Step<Plan>, ExecutionStep {

	/**
	 * Take an operational state transformation step
	 */
	fun take(state: OperationalState): OperationalState

	override fun take(state: Plan): Plan =
			Plan(
					states = state.states + take(state.state),
					steps = state.steps + this
			)

	/**
	 * Get the list of step types which should follow this step (otherwise this step does not make sense)
	 */
	@get:JsonIgnore
	val useBefore: List<KClass<out Step<*>>>?
		get() = null

	/**
	 * Get the list of costs expected at executing this step.
	 * Default implementation returns an empty list, meaning negligible
	 * costs.
	 */
	@JsonIgnore
	fun getCost(): List<Cost> = listOf()

	/**
	 * List both the physical and virtual resources reserved for the execution of the step.
	 *
	 * This method does not have a default implementation since I have frequently made mistakes
	 * by leaving the default implementation. Actually the default empty list rarely makes sense.
	 */
	fun reservations(): List<Reservation<*>>
}