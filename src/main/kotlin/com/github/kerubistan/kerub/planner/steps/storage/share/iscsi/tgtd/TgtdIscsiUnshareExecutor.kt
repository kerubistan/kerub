package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.iscsi.tgtd.TgtAdmin

class TgtdIscsiUnshareExecutor(
		private val hostConfigDao: HostConfigurationDao,
		private val hostExecutor: HostCommandExecutor
) : AbstractStepExecutor<TgtdIscsiUnshare, Unit>() {
	override fun update(step: TgtdIscsiUnshare, updates: Unit) {
		hostConfigDao.update(step.host.id) {
			it.copy(
					services = it.services.filterNot { it is IscsiService && it.vstorageId == step.vstorage.id }
			)
		}
	}

	override fun perform(step: TgtdIscsiUnshare) {
		hostExecutor.execute(step.host) { TgtAdmin.unshareBlockDevice(it, step.vstorage.id) }
	}
}