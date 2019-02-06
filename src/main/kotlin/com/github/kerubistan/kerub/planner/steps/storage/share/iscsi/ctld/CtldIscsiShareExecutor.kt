package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.AbstractIscsiExecutor
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd.TgtdIscsiShare
import com.github.kerubistan.kerub.utils.junix.iscsi.ctld.Ctld

class CtldIscsiShareExecutor(hostConfigDao: HostConfigurationDao,
							 hostExecutor: HostCommandExecutor,
							 hostManager: HostManager) : AbstractIscsiExecutor<TgtdIscsiShare>(hostConfigDao, hostExecutor, hostManager) {
	override fun perform(step: TgtdIscsiShare, password: String) {
		hostExecutor.execute(host = step.host) {
			session ->
			Ctld.share(
					session = session,
					id = step.vstorage.id,
					readOnly = step.vstorage.readOnly,
					path = step.allocation.getPath(step.vstorage.id)
			)
			hostManager.getServiceManager(host = step.host).let {
				it.enable(Ctld)
				it.start(Ctld)
			}
		}
	}
}