package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi.tgtd

import com.github.K0zka.kerub.data.config.HostConfigurationDao
import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin

class TgtdIscsiShareExecutor(
		private val hostConfigDao: HostConfigurationDao,
		private val hostExecutor: HostCommandExecutor,
		private val hostManager: HostManager)
: AbstractStepExecutor<TgtdIscsiShare, Unit>() {
	override fun update(step: TgtdIscsiShare, updates: Unit) {
		hostConfigDao.update(step.host.id) {
			config ->
			config.copy(
					services = config.services + IscsiService(vstorageId = step.vstorage.id)
			)
		}
	}

	override fun perform(step: TgtdIscsiShare) {
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
					id = step.vstorage.id
			)
		}
	}
}