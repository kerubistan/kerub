package com.github.kerubistan.kerub.planner.steps.vm.common

import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.config.OvsDataPort
import com.github.kerubistan.kerub.model.config.OvsNetworkConfiguration
import com.github.kerubistan.kerub.model.devices.NetworkDevice
import com.github.kerubistan.kerub.utils.mapInstances
import java.util.UUID

fun getVirtualNetworkConnections(vm: VirtualMachine, hostData: HostDataCollection) =
		vm.devices.mapInstances { device: NetworkDevice -> device.networkId }.map { networkId ->
			networkId to hostData.config?.index?.ovsNetworkConfigurations?.get(networkId)
		}.toMap()

fun allNetworksAvailable(
		vm: VirtualMachine,
		virtualNetworkConnections: Map<UUID, OvsNetworkConfiguration?>
) = virtualNetworkConnections.all { (_, value) ->
	value != null && value.ports.any { port -> port is OvsDataPort && port.name == vm.idStr }
}
