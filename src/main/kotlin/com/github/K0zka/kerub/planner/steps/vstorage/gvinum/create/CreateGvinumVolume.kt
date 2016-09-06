package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.GvinumConfiguration
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage
import com.github.K0zka.kerub.utils.update

data class CreateGvinumVolume(
		override val host: Host,
		override val disk: VirtualStorageDevice,
		val config: GvinumConfiguration
) : AbstractCreateVirtualStorage {

	override fun take(state: OperationalState): OperationalState {
		require(host.capabilities?.os == OperatingSystem.BSD) {
			"Need BSD operating system, got ${host.capabilities?.os}"
		}
		require(host.capabilities?.distribution?.name == "FreeBSD", {
			"Gvinum runs on FreeBSD, got ${host.capabilities?.distribution?.name}"
		})
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
									actualSize = disk.size,
									lastUpdated = System.currentTimeMillis(),
									allocation = VirtualStorageGvinumAllocation(hostId = host.id, configuration = config)
							)
					)
				}
		)
	}
}