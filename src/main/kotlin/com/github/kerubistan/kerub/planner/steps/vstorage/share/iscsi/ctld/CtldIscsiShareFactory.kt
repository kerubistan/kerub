package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.utils.unsharedDisks
import com.github.kerubistan.kerub.utils.junix.iscsi.ctld.Ctld

object CtldIscsiShareFactory : AbstractOperationalStepFactory<CtldIscsiShare>() {
	override fun produce(state: OperationalState): List<CtldIscsiShare> =
			unsharedDisks(state).filter {
				diskData ->
				val allocation = diskData.dynamic?.allocation
				val hostId = allocation?.hostId
				allocation is VirtualStorageBlockDeviceAllocation && state.hosts[hostId]?.let {
					val capabilities = it.stat.capabilities
					capabilities?.os == OperatingSystem.BSD
							&& capabilities.distribution?.name == "FreeBSD"
							&& Ctld.available(capabilities)
				} ?: false
			}.map {
				CtldIscsiShare(
						host = requireNotNull(state.hosts[it.dynamic?.allocation?.hostId]).stat,
						vstorage = it.stat
				)
			}
}