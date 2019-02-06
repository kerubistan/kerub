package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.data.config.HostConfigurationDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.host.HostManager
import com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.AbstractIscsiExecutor
import com.github.kerubistan.kerub.utils.junix.iscsi.tgtd.TgtAdmin

class TgtdIscsiShareExecutor(
		hostConfigDao: HostConfigurationDao,
		hostExecutor: HostCommandExecutor,
		hostManager: HostManager)
	: AbstractIscsiExecutor<TgtdIscsiShare>(hostConfigDao, hostExecutor, hostManager) {

	override fun perform(step: TgtdIscsiShare, password: String) {
		hostExecutor.execute(step.host) {
			session ->

			hostManager.getServiceManager(step.host).start(TgtAdmin)
			hostManager.getFireWall(step.host).open(
					port = 3260,
					proto = "tcp"
			)
			TgtAdmin.shareBlockDevice(
					session = session,
					path = step.allocation.getPath(step.vstorage.id),
					readOnly = step.vstorage.readOnly,
					id = step.vstorage.id,
					password = password
			)
		}
	}
}