package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.GvinumStorageCapability
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorageFactory

import com.github.K0zka.kerub.utils.join

object CreateGvinumVolumeFactory : AbstractCreateVirtualStorageFactory<CreateGvinumVolume>() {
	override fun produce(state: OperationalState): List<CreateGvinumVolume> =
			listStorageNotAllocated(state).map {
				virtualStorage ->

				state.hosts.values.filter {
					host ->
					// filter for FreeBSD servers
					// which have gvinum storage
					host.capabilities?.os == OperatingSystem.BSD
							&& host.capabilities?.distribution?.name == "FreeBSD"
							&& host.capabilities?.storageCapabilities?.any {
						storage ->
						storage is GvinumStorageCapability
					} ?: false
				}.map {
					host ->
					host.capabilities?.storageCapabilities?.filter { it is GvinumStorageCapability }?.map {
						CreateGvinumVolume(
								host = host,
								disk = virtualStorage
						)
					} ?: listOf()
				}.join()
			}.join()
}