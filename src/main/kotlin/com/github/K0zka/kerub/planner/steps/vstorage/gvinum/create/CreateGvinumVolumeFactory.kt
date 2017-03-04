package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.collection.HostDataCollection
import com.github.K0zka.kerub.model.dynamic.gvinum.ConcatenatedGvinumConfiguration
import com.github.K0zka.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.factoryFeature
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory
import com.github.K0zka.kerub.utils.join
import java.math.BigInteger

object CreateGvinumVolumeFactory : AbstractCreateVirtualStorageFactory<CreateGvinumVolume>() {

	override fun produce(state: OperationalState): List<CreateGvinumVolume> =
			factoryFeature(state.controllerConfig.gvinumCreateVolumeEnabled) {
				listStorageNotAllocated(state).map {
					virtualStorage ->

					hostsWithGvinumCapabilities(state).map {
						host ->

						simpleGvinumAllocations(host, virtualStorage) +
								concatenatedGvinumAllocations(host, virtualStorage)

					}.join()
				}.join()
			}


	internal fun concatenatedGvinumAllocations(host: HostDataCollection, virtualStorage: VirtualStorageDevice): List<CreateGvinumVolume> {
		return combineConcatenations(
				host = host,
				size = virtualStorage.size
		).map {
			concat ->
			CreateGvinumVolume(
					host = host.stat,
					disk = virtualStorage,
					config = ConcatenatedGvinumConfiguration(
							disks = concat
					)
			)
		}
	}

	internal fun combineConcatenations(host: HostDataCollection, size: BigInteger): List<Map<String, BigInteger>> {
		val gvinumDisks = gvinumCapabilities(host.stat).map {
			cap ->
			val storageDyn = host.dynamic?.storageStatus?.firstOrNull { cap.id == it.id }
			if (storageDyn == null) {
				null
			} else {
				cap.name to storageDyn.freeCapacity
			}
		}.filterNotNull().toMap()
		return combineConcatenations(gvinumDisks, size)
	}

	private fun combineConcatenations(gvinumDisks: Map<String, BigInteger>, size: BigInteger): List<Map<String, BigInteger>> {
		return gvinumDisks.map {
			diskCap ->
			if (diskCap.value > size) {
				listOf(mapOf(diskCap.key to size))
			} else {
				combineConcatenations(
						gvinumDisks = gvinumDisks.filterKeys { it != diskCap.key },
						size = size - diskCap.value
				).map { it + (diskCap.key to diskCap.value) }
			}
		}.filterNotNull().join().toSet().toList()
	}

	private fun simpleGvinumAllocations(host: HostDataCollection, virtualStorage: VirtualStorageDevice): List<CreateGvinumVolume> {
		return filterByFreeSpace(
				host = host,
				size = virtualStorage.size,
				capabilities = filterBySize(gvinumCapabilities(host.stat), virtualStorage.size)
		).map {
			gvinumCapability ->

			CreateGvinumVolume(
					host = host.stat,
					disk = virtualStorage,
					config = SimpleGvinumConfiguration(
							diskId = gvinumCapability.id
					)
			)
		}
	}

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
					&& host.stat.capabilities.distribution?.name == "FreeBSD"
					&& host.stat.capabilities.storageCapabilities.any {
				storage ->
				storage is GvinumStorageCapability
			}
		}
	}
}