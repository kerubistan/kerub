package com.github.kerubistan.kerub.planner.steps.storage.gvinum.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.GvinumStorageCapability
import com.github.kerubistan.kerub.model.GvinumStorageCapabilityDrive
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.gvinum.ConcatenatedGvinumConfiguration
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.storage.AbstractCreateVirtualStorageFactory
import io.github.kerubistan.kroki.collections.join
import java.math.BigInteger
import kotlin.reflect.KClass

object CreateGvinumVolumeFactory : AbstractCreateVirtualStorageFactory<CreateGvinumVolume>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override fun produce(state: OperationalState): List<CreateGvinumVolume> =
			factoryFeature(state.controllerConfig.storageTechnologies.gvinumCreateVolumeEnabled) {
				listStorageNotAllocated(state).map { virtualStorage ->

					hostsWithGvinumCapabilities(state).map { host ->

						simpleGvinumAllocations(host, virtualStorage) +
								concatenatedGvinumAllocations(host, virtualStorage)

					}.join()
				}.join()
			}


	private fun concatenatedGvinumAllocations(
			host: HostDataCollection, virtualStorage: VirtualStorageDevice
	): List<CreateGvinumVolume> =
			combineConcatenations(
					host = host,
					size = virtualStorage.size
			).map { concat ->
				CreateGvinumVolume(
						host = host.stat,
						disk = virtualStorage,
						config = ConcatenatedGvinumConfiguration(
								disks = concat
						),
						capability = requireNotNull(
								host.stat.capabilities?.storageCapabilities
										?.filterIsInstance<GvinumStorageCapability>()?.single())
				)
			}

	private fun combineConcatenations(host: HostDataCollection, size: BigInteger): List<Map<String, BigInteger>> =
			gvinumCapability(host.stat)?.let { cap ->
				val storageDyn = host.dynamic?.storageStatus?.firstOrNull { cap.id == it.id }
				cap.devices.mapNotNull { drive ->
					if (storageDyn == null) {
						null
					} else {
						drive.name to storageDyn.freeCapacity
					}
				}.toMap()
			}?.let { diskFreeMap -> combineConcatenations(diskFreeMap, size) } ?: listOf()

	private fun combineConcatenations(
			gvinumDisks: Map<String, BigInteger>, size: BigInteger
	): List<Map<String, BigInteger>> =
			gvinumDisks.map { diskCap ->
				if (diskCap.value > size) {
					listOf(mapOf(diskCap.key to size))
				} else {
					combineConcatenations(
							gvinumDisks = gvinumDisks.filterKeys { it != diskCap.key },
							size = (size - diskCap.value).coerceAtLeast(BigInteger.ZERO)
					).map { it + (diskCap.key to diskCap.value) }
				}
			}.join().toSet().toList()

	private fun simpleGvinumAllocations(
			host: HostDataCollection, virtualStorage: VirtualStorageDevice
	): List<CreateGvinumVolume> =
			gvinumCapability(host.stat)?.let { capability ->
				filterByFreeSpace(
						host = host,
						size = virtualStorage.size,
						capability = gvinumCapability(host.stat)
				).let { drives ->
					drives.map { drive ->
						CreateGvinumVolume(
								host = host.stat,
								disk = virtualStorage,
								config = SimpleGvinumConfiguration(
										diskName = drive.name
								),
								capability = capability
						)
					}
				}
			} ?: listOf()

	private fun gvinumCapability(host: Host): GvinumStorageCapability? =
			host.capabilities?.storageCapabilities?.filterIsInstance<GvinumStorageCapability>()?.singleOrNull()

	private fun filterBySize(
			drives: List<GvinumStorageCapabilityDrive>,
			size: BigInteger
	): List<GvinumStorageCapabilityDrive> = drives.filter { it.size > size }

	private fun filterByFreeSpace(
			host: HostDataCollection,
			capability: GvinumStorageCapability?,
			size: BigInteger
	): List<GvinumStorageCapabilityDrive> =
			capability?.devices?.filter {
				(host.dynamic?.storageStatusById?.get(capability.id)?.freeCapacity ?: BigInteger.ZERO) > size
			}
					?: listOf()

	private fun hostsWithGvinumCapabilities(state: OperationalState): List<HostDataCollection> {
		return state.hosts.values.filter { host ->
			// filter for FreeBSD servers
			// which have gvinum storage
			host.stat.capabilities?.os == OperatingSystem.BSD
					&& host.stat.capabilities.distribution?.name == "FreeBSD"
					&& host.stat.capabilities.storageCapabilities.any { storage ->
				storage is GvinumStorageCapability
			}
		}
	}
}