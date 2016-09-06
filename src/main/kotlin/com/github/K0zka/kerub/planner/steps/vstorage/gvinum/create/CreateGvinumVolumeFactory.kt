package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.collection.HostDataCollection
import com.github.K0zka.kerub.model.dynamic.SimpleGvinumConfiguration
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory

import com.github.K0zka.kerub.utils.join
import java.math.BigInteger

object CreateGvinumVolumeFactory : AbstractCreateVirtualStorageFactory<CreateGvinumVolume>() {
	override fun produce(state: OperationalState): List<CreateGvinumVolume> =
			listStorageNotAllocated(state).map {
				virtualStorage ->

				hostsWithGvinumCapabilities(state).map {
					host ->
					filterByFreeSpace(
							host = host,
							state = state,
							size = virtualStorage.size,
							capabilities = filterBySize(gvinumCapabilities(host.stat), virtualStorage.size)).map {
						gvinumCapability ->

						CreateGvinumVolume(
								host = host.stat,
								disk = virtualStorage,
								config = SimpleGvinumConfiguration(
										diskId = (gvinumCapability as GvinumStorageCapability).id
								)
						)
					}
				}.join()
			}.join()

	private fun gvinumCapabilities(host: Host): List<GvinumStorageCapability> {
		return host.capabilities?.storageCapabilities?.filter {
			it is GvinumStorageCapability
		}?.map {
			it as GvinumStorageCapability
		} ?: listOf()
	}

	private fun filterBySize(
			capabilities: List<GvinumStorageCapability>,
			size: BigInteger): List<GvinumStorageCapability>
			= capabilities.filter { it.size > size }

	private fun filterByFreeSpace(
			host: HostDataCollection,
			state: OperationalState,
			capabilities: List<GvinumStorageCapability>,
			size: BigInteger
	): List<GvinumStorageCapability> {
		return capabilities.filter {
			capability ->
			val dyn = host.dynamic?.storageStatus?.firstOrNull { it.id == capability.id }
			(dyn?.freeCapacity ?: BigInteger.ZERO) > size
		}
	}

	private fun hostsWithGvinumCapabilities(state: OperationalState): List<HostDataCollection> {
		return state.hosts.values.filter {
			host ->
			// filter for FreeBSD servers
			// which have gvinum storage
			host.stat.capabilities?.os == OperatingSystem.BSD
					&& host.stat.capabilities?.distribution?.name == "FreeBSD"
					&& host.stat.capabilities?.storageCapabilities?.any {
				storage ->
				storage is GvinumStorageCapability
			} ?: false
		}
	}
}