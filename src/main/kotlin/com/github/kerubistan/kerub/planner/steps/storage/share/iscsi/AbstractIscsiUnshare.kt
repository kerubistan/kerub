package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi

import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import io.github.kerubistan.kroki.collections.update

interface AbstractIscsiUnshare : AbstractIscsiOperation {
	override fun take(state: OperationalState): OperationalState = state.copy(
			hosts = state.hosts.update(host.id) { hostData ->
				val hostConfig = hostData.config
				hostData.copy(
						config = hostConfig?.copy(
								services = hostConfig.services.filterNot { it is IscsiService && it.vstorageId == vstorage.id })
				)
			}
	)
}