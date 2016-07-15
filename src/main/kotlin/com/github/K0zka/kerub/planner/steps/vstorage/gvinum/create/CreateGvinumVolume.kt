package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.vstorage.AbstractCreateVirtualStorage

data class CreateGvinumVolume(
		override val host: Host,
		override val disk: VirtualStorageDevice

) : AbstractCreateVirtualStorage {

	override fun take(state: OperationalState): OperationalState {
		require(host.capabilities?.os == OperatingSystem.BSD) {
			"Need BSD operating system, got ${host.capabilities?.os}"
		}
		require(host.capabilities?.distribution?.name == "FreeBSD", {
			"Gvinum runs on FreeBSD, got ${host.capabilities?.distribution?.name}"
		})
		TODO("not implemented")
	}
}