package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiExecutor

class TgtdIscsiUnshareExecutor(
		hostConfigDao: HostConfigurationDao,
		hostExecutor: HostCommandExecutor,
		hostManager: HostManager
) : AbstractIscsiExecutor<TgtdIscsiUnshare>(hostConfigDao, hostExecutor, hostManager) {
	override fun perform(step: TgtdIscsiUnshare, password: String) {
		TODO()
	}
}