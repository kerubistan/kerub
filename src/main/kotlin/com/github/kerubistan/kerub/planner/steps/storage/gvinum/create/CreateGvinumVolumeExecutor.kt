package com.github.kerubistan.kerub.planner.steps.storage.gvinum.create

import com.github.kerubistan.kerub.data.dynamic.HostDynamicDao
import com.github.kerubistan.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.ConcatenatedGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.MirroredGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.StripedGvinumConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.gvinum.GVinum
import com.github.kerubistan.kerub.utils.sumBy

class CreateGvinumVolumeExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualDiskDynDao: VirtualStorageDeviceDynamicDao,
		private val hostDynamicDao: HostDynamicDao
) : AbstractStepExecutor<CreateGvinumVolume, Unit>() {
	override fun update(step: CreateGvinumVolume, updates: Unit) {
		virtualDiskDynDao.update(id = step.disk.id, retrieve = {
			virtualDiskDynDao[step.disk.id] ?: VirtualStorageDeviceDynamic(
					id = step.disk.id,
					allocations = listOf()
			)

		}, change = {
			transformVirtualStorageDynamic(it, step)
		})
		hostCommandExecutor.execute(step.host) { session ->
			val updatedDisks = GVinum.listDrives(session).filter { drive ->
				when (step.config) {
					is SimpleGvinumConfiguration -> step.config.diskName == drive.name
					is ConcatenatedGvinumConfiguration -> drive.name in step.config.disks.keys
					is MirroredGvinumConfiguration -> drive.name in step.config.disks
					is StripedGvinumConfiguration -> drive.name in step.config.disks
					else -> TODO("Not handled gvinum configuration type: ${step.config}")
				}
			}
			val updatedDisksByName = lazy { updatedDisks.associateBy { it.name } }
			hostDynamicDao.update(step.host.id) {
				it.copy(
						storageStatus = it.storageStatus.map { deviceDynamic ->
							if (deviceDynamic.id == step.capability.id) {
								when (deviceDynamic) {
									is SimpleStorageDeviceDynamic -> deviceDynamic.copy(
											freeCapacity = updatedDisks.sumBy { it.available }
									)
									is CompositeStorageDeviceDynamic -> deviceDynamic.copy(
											items = deviceDynamic.items.map { item ->
												if (updatedDisksByName.value.containsKey(item.name)) {
													item.copy(
															freeCapacity =
															requireNotNull(updatedDisksByName.value[item.name]).available
													)
												} else {
													item
												}
											}
									)
									else -> TODO("Not handled deviceDynamic type: $deviceDynamic")
								}
							} else {
								deviceDynamic
							}
						}
				)
			}
		}
	}

	fun transformVirtualStorageDynamic(dynamic: VirtualStorageDeviceDynamic, step: CreateGvinumVolume): VirtualStorageDeviceDynamic {
		return dynamic.copy(
				allocations = dynamic.allocations + VirtualStorageGvinumAllocation(
						hostId = step.host.id,
						actualSize = step.disk.size,
						configuration = step.config,
						capabilityId = step.capability.id
				)
		)
	}

	override fun perform(step: CreateGvinumVolume) {
		hostCommandExecutor.execute(step.host) { session ->
			when (step.config) {
				is ConcatenatedGvinumConfiguration -> {
					GVinum.createConcatenatedVolume(
							session = session,
							volName = step.disk.id.toString(),
							disks = step.config.disks
					)
				}
				is SimpleGvinumConfiguration -> {
					GVinum.createSimpleVolume(
							session = session,
							volName = step.disk.id.toString(),
							disk = step.config.diskName,
							size = step.disk.size)
				}
				else -> {
					TODO("gvinum configuration not implemented by executor: ${step.config}")
				}
			}
		}
	}

}