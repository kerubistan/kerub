package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.dynamic.VirtualStorageBlockDeviceAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.utils.unsharedDisks
import com.github.K0zka.kerub.utils.junix.iscsi.ctld.Ctld

object CtldIscsiShareFactory : AbstractOperationalStepFactory<CtldIscsiShare>() {
	override fun produce(state: OperationalState): List<CtldIscsiShare> =
			unsharedDisks(state).filter {
				diskData ->
				val allocation = diskData.dynamic?.allocation
				val hostId = allocation?.hostId
				allocation is VirtualStorageBlockDeviceAllocation && state.hosts[hostId]?.let {
					val capabilities = it.stat.capabilities
					capabilities?.os == OperatingSystem.BSD
							&& capabilities?.distribution?.name == "FreeBSD"
							&& Ctld.available(capabilities)
				} ?: false
			}.map {
				CtldIscsiShare(
						host = requireNotNull(state.hosts[it.dynamic?.allocation?.hostId]).stat,
						vstorage = it.stat
				)
			}
}