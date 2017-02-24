package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.utils.unsharedDisks
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin

object TgtdIscsiShareFactory : AbstractOperationalStepFactory<TgtdIscsiShare>() {

	override fun produce(state: OperationalState): List<TgtdIscsiShare> {
		return unsharedDisks(state).map {
			storageData ->
			val host = requireNotNull(state.hosts[storageData.dynamic?.allocation?.hostId]?.stat)
			if (TgtAdmin.available(host.capabilities)) {
				TgtdIscsiShare(
						host = host,
						vstorage = storageData.stat,
						devicePath = (storageData.dynamic?.allocation as VirtualStorageLvmAllocation).path
				)
			} else null
		}.filterNotNull()
	}

}