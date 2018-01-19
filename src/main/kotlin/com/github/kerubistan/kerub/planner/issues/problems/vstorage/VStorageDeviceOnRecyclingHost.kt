package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageAllocation
import com.github.kerubistan.kerub.planner.issues.problems.Problem

data class VStorageDeviceOnRecyclingHost(
		val vstorage: VirtualStorageDevice,
		val allocation: VirtualStorageAllocation,
		val host : Host
) : Problem