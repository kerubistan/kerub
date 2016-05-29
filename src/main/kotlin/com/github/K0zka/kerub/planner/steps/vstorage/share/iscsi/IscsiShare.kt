package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.utils.update

data class IscsiShare(val host: Host, val vstorage: VirtualStorageDevice, val devicePath : String) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState
			= state.copy(
			hostDyns = state.hostDyns.update(host.id, {
				hostDyn ->
				hostDyn.copy(
						services = hostDyn.services + (IscsiService(vstorage.id))
				)
			})
	)
}