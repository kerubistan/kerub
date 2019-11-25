package com.github.kerubistan.kerub.planner.steps.network.ovs.port.create

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ovs.Vsctl
import io.github.kerubistan.kroki.collections.updateInstances

class CreateOvsPortExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostConfigurationDao: HostConfigurationDao
) : AbstractStepExecutor<CreateOvsPort, Unit>() {

	override fun perform(step: CreateOvsPort) {
		hostCommandExecutor.execute(step.host) { session ->
			Vsctl.createInternalPort(session, step.virtualNetwork.idStr, step.portName)
		}
	}

	override fun update(step: CreateOvsPort, updates: Unit) {
		hostConfigurationDao.update(step.host.id) { hostConfig ->
			hostConfig.copy(
					networkConfiguration = hostConfig.networkConfiguration.updateInstances(
							selector = { ovsConfig: OvsNetworkConfiguration ->
								ovsConfig.virtualNetworkId == step.virtualNetwork.id
							},
							map = { ovsConfig ->
								ovsConfig.copy(
										ports = ovsConfig.ports + OvsDataPort(name = step.portName)
								)
							}
					)
			)
		}
	}
}