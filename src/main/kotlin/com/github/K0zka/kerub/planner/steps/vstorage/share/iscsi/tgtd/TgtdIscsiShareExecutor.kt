package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiExecutor
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin

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
					path = step.devicePath,
					readOnly = step.vstorage.readOnly,
					id = step.vstorage.id,
					password = password
			)
		}
	}
}