package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.config.HostConfiguration
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStep
import com.github.K0zka.kerub.utils.update

data class TgtdIscsiShare(val host: Host, val vstorage: VirtualStorageDevice, val devicePath: String) : AbstractOperationalStep {
	override fun take(state: OperationalState): OperationalState
			= state.copy(
			hosts = state.hosts.update(host.id, {
				hostData ->
				val hostConfig = hostData.config
				val service = IscsiService(vstorage.id)
				hostData.copy(
						config = updateHostConfig(hostConfig, service)
				)
			})
	)

	private fun updateHostConfig(hostConfig: HostConfiguration?, service: IscsiService): HostConfiguration {
		return if (hostConfig == null) {
			HostConfiguration(id = host.id, services = listOf(service))
		} else {
			hostConfig.copy(
					services = hostConfig.services + service)
		}
	}

	override fun toString() = "TgtdIscsiShare(host=${host.address} (${host.id})," +
			"vstorage=${vstorage.name} (${vstorage.id})," +
			"path=$devicePath)"
}