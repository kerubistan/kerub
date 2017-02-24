package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.AbstractIscsiExecutor
import com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd.TgtdIscsiShare
import com.github.K0zka.kerub.utils.junix.iscsi.ctld.Ctld

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
					path = "/dev/gvinum/${step.vstorage.id}"
			)
			hostManager.getServiceManager(host = step.host).let {
				it.enable(Ctld)
				it.start(Ctld)
			}
		}
	}
}