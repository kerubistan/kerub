package com.github.kerubistan.kerub.planner.steps.network.ovs.port.remove

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.ovs.Vsctl
import io.github.kerubistan.kroki.collections.updateInstances

class RemoveOvsPortExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val hostConfigurationDao: HostConfigurationDao
) : AbstractStepExecutor<RemoveOvsPort, Unit>() {

	override fun perform(step: RemoveOvsPort) {
		hostCommandExecutor.execute(step.host) { session ->
			Vsctl.removePort(session, step.virtualNetwork.idStr, step.portName)
		}
	}

	override fun update(step: RemoveOvsPort, updates: Unit) {
		hostConfigurationDao.update(step.host.id) { config ->
			config.copy(
					networkConfiguration =
					config.networkConfiguration.updateInstances(
							selector = { networkConfig: OvsNetworkConfiguration ->
								networkConfig.virtualNetworkId == step.virtualNetwork.id
							},
							map = { networkConfig ->
								networkConfig.copy(
										ports = networkConfig.ports.filterNot { it.name == step.portName }
								)
							}
					)
			)
		}
	}
}