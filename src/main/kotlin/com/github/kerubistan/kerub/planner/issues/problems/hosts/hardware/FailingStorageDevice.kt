package com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.StorageCapability
import com.github.kerubistan.kerub.planner.issues.problems.Problem

data class FailingStorageDevice(
		val host: Host,
		val storageCapability: StorageCapability,
		val device: String
) : Problem {
	init {
		check(storageCapability in checkNotNull(host.capabilities).storageCapabilities) {
			"storage capability $storageCapability not registered in host ${host.address} ${host.id}"
		}
	}
}