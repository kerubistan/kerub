package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.GvinumConfiguration
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage

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
		val hostDyn = requireNotNull(state.hostDyns[host.id]) {
			"Host dynamic not found - host must be running"
		}
		return state.copy(
				hostDyns = state.hostDyns + (host.id to hostDyn.copy(
						storageStatus = hostDyn.storageStatus // TODO tell about the storage
				)),
				vStorageDyns = state.vStorageDyns + (disk.id to VirtualStorageDeviceDynamic(
						id = disk.id,
						actualSize = disk.size,
						lastUpdated = System.currentTimeMillis(),
						allocation = VirtualStorageGvinumAllocation(hostId = host.id, configuration = config)
				))
		)
	}
}