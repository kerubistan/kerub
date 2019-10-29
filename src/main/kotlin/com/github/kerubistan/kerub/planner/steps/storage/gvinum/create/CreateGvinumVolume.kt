package com.github.kerubistan.kerub.planner.steps.storage.gvinum.create

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.ConcatenatedGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.GvinumConfiguration
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.costs.Cost
import com.github.kerubistan.kerub.planner.costs.Risk
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorage
import com.github.kerubistan.kerub.utils.update
import io.github.kerubistan.kroki.time.now

@JsonTypeName("create-gvinum-volume")
data class CreateGvinumVolume(
		override val host: Host,
		override val capability: GvinumStorageCapability,
		override val disk: VirtualStorageDevice,
		val config: GvinumConfiguration
) : AbstractCreateVirtualStorage<VirtualStorageGvinumAllocation, GvinumStorageCapability> {

	init {
		check(capability in host.capabilities?.storageCapabilities ?: listOf()) {
			"Capability must be registered in the host"
		}
		check(capability.devicesByName.keys.containsAll(config.diskNames)) {
			"Not all of ${config.diskNames} could be found in ${capability.devicesByName.keys}"
		}
	}

	@get:JsonIgnore
	override val format: VirtualDiskFormat
		get() = VirtualDiskFormat.raw

	@get:JsonIgnore
	override val allocation: VirtualStorageGvinumAllocation by lazy {
		VirtualStorageGvinumAllocation(
				hostId = host.id,
				actualSize = disk.size,
				configuration = config,
				capabilityId = capability.id
		)
	}

	override fun getCost(): List<Cost> {
		return when (config) {
			is ConcatenatedGvinumConfiguration -> {
				listOf<Cost>(
						Risk(
								score = maxOf(config.disks.size - 1, 1),
								comment = "Loss of any of the ${config.disks.size} disks causes failure of the vstorage"
						)
				)
			}
			else -> {
				listOf()
			}
		}
	}

	override fun take(state: OperationalState): OperationalState {
		require(host.capabilities?.os == OperatingSystem.BSD) {
			"Need BSD operating system, got ${host.capabilities?.os}"
		}
		require(host.capabilities?.distribution?.name == "FreeBSD") {
			"Gvinum runs on FreeBSD, got ${host.capabilities?.distribution?.name}"
		}
		val hostDyn = requireNotNull(state.hosts[host.id]?.dynamic) {
			"Host dynamic not found - host must be running"
		}
		return state.copy(
				hosts = state.hosts.update(host.id) {
					hostData ->
					hostData.copy(
							dynamic = hostDyn.copy(
									storageStatus = hostDyn.storageStatus // TODO tell about the storage
							)
					)
				},
				vStorage = state.vStorage.update(disk.id) {
					vStorageData ->
					vStorageData.copy(
							dynamic = VirtualStorageDeviceDynamic(
									id = disk.id,
									lastUpdated = now(),
									allocations = listOf(allocation)
							)
					)
				}
		)
	}
}