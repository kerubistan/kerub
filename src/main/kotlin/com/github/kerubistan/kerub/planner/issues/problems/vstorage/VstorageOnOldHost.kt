package com.github.kerubistan.kerub.planner.issues.problems.vstorage

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.planner.issues.problems.HostProblem

data class VstorageOnOldHost(
		override val host: Host,
		val vStorage : VirtualStorageDevice,
		val hostExpiredSince: Int
) : HostProblem