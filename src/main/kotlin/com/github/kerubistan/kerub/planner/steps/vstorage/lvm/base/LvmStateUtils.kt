package com.github.kerubistan.kerub.planner.steps.vstorage.lvm.base

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.replace
import java.math.BigInteger

fun updateHostDynLvmWithAllocation(state: OperationalState, host: Host, volumeGroupName: String, size: BigInteger): HostDynamic {

	val originalHostDyn = requireNotNull(state.hosts[host.id]?.dynamic)
	val volGroup = requireNotNull(host.capabilities?.storageCapabilities?.first { it is LvmStorageCapability && it.volumeGroupName == volumeGroupName })
	return originalHostDyn.copy(
			storageStatus = originalHostDyn.storageStatus.replace({ it.id == volGroup.id }, {
				it.copy(
						freeCapacity = (it.freeCapacity - size)
				)
			})
	)
}
