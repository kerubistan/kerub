package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.utils.unsharedDisks
import com.github.kerubistan.kerub.utils.junix.iscsi.tgtd.TgtAdmin

object TgtdIscsiShareFactory : AbstractOperationalStepFactory<TgtdIscsiShare>() {

	override fun produce(state: OperationalState): List<TgtdIscsiShare> {
		return unsharedDisks(state).map {
			storageData ->
			val host = requireNotNull(state.hosts[storageData.dynamic?.allocation?.hostId])
			if (TgtAdmin.available(host.stat.capabilities) && host.dynamic?.status == HostStatus.Up) {
				TgtdIscsiShare(
						host = host.stat,
						vstorage = storageData.stat,
						devicePath = (storageData.dynamic?.allocation as VirtualStorageLvmAllocation).path
				)
			} else null
		}.filterNotNull()
	}

}