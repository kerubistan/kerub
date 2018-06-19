package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.planner.steps.factoryFeature
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.utils.iscsiShareableDisks
import com.github.kerubistan.kerub.utils.join
import com.github.kerubistan.kerub.utils.junix.iscsi.ctld.Ctld
import java.util.UUID

object CtldIscsiShareFactory : AbstractOperationalStepFactory<CtldIscsiShare>() {
	override fun produce(state: OperationalState): List<CtldIscsiShare> =
			factoryFeature(state.controllerConfig.storageTechnologies.iscsiEnabled) {
				iscsiShareableDisks(state).mapNotNull { (disk, allocations) ->
					allocations.filter {
						isCtldAvailable(state, it.hostId)
					}.map {
						CtldIscsiShare(
								host = requireNotNull(state.hosts[it.hostId]).stat,
								vstorage = disk.stat,
								allocation = it
						)
					}

				}.join()
			}

	fun isCtldAvailable(state: OperationalState, hostId: UUID): Boolean =
			state.hosts[hostId]?.let {
						Ctld.available(it.stat.capabilities)
			} ?: false
}