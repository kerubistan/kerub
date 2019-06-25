package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.utils.contains
import com.github.kerubistan.kerub.utils.hasAny

class OperationalStateIndex(val indexOf: OperationalState) {

	val runningHosts by lazy { indexOf.hosts.values.filter { it.dynamic?.status == HostStatus.Up } }

	val runningHostIds by lazy { runningHosts.map { it.stat.id }.toSet() }

	val connectionTargets by lazy {
		runningHosts.mapNotNull { client ->
			if (client.config?.publicKey == null)
				null
			else {
				val acceptedByServer = runningHosts.mapNotNull { server ->
					if (client.config.publicKey in server.config?.acceptedPublicKeys)
						server.stat
					else null
				}
				if (acceptedByServer.isEmpty()) null else client.id to acceptedByServer
			}
		}.toMap()
	}


	val recyclingHosts by lazy {
		indexOf.hosts.values
				.filter { it.stat.recycling }
				.associateBy { it.stat.id }
	}

	val runningVms by lazy {
		indexOf.vms.values.filter {
			it.dynamic?.status == VirtualMachineStatus.Up
		}
	}

	val allocatedStorage by lazy {
		indexOf.vStorage.values.filter { it.dynamic?.allocations?.isNotEmpty() ?: false }
	}

	val storageCloneRequirement by lazy {
		indexOf.vStorage.values.filter { it.stat.expectations.hasAny<CloneOfStorageExpectation>() }
	}

	val vmsThatMustStart by lazy {
		indexOf.vms.values.filter { vm ->
			vm.stat.expectations.any { expectation ->
				expectation is VirtualMachineAvailabilityExpectation
						&& expectation.up
			} && vm.dynamic?.status != VirtualMachineStatus.Up
		}
	}


}