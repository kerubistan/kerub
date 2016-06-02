package com.github.K0zka.kerub.planner.steps.vstorage.share.iscsi

import com.github.K0zka.kerub.data.dynamic.HostDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.host.HostManager
import com.github.K0zka.kerub.model.services.IscsiService
import com.github.K0zka.kerub.planner.execution.AbstractStepExecutor
import com.github.K0zka.kerub.utils.junix.iscsi.tgtd.TgtAdmin

class IscsiShareExecutor(
		private val hostDynamicDao: HostDynamicDao,
		private val hostExecutor: HostCommandExecutor,
		private val hostManager: HostManager)
: AbstractStepExecutor<IscsiShare, Unit>() {
	override fun update(step: IscsiShare, updates: Unit) {
		hostDynamicDao.update(step.host.id) {
			it.copy(
					services = it.services + IscsiService(vstorageId = step.vstorage.id)
			)
		}
	}

	override fun perform(step: IscsiShare) {
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