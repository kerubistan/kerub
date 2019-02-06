package com.github.kerubistan.kerub.planner.steps.storage.lvm.vg

import com.github.kerubistan.kerub.data.HostDao
import com.github.kerubistan.kerub.host.HostCommandExecutor
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.planner.execution.AbstractStepExecutor
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmPv
import com.github.kerubistan.kerub.utils.junix.storagemanager.lvm.LvmVg
import java.math.BigInteger

class RemoveDiskFromVGExecutor(private val hostCommandExecutor: HostCommandExecutor, private val hostDao: HostDao) : AbstractStepExecutor<RemoveDiskFromVG, BigInteger>() {
	override fun perform(step: RemoveDiskFromVG): BigInteger =
			hostCommandExecutor.execute(step.host) { session ->
				LvmPv.move(session = session, pv = step.device)
				LvmVg.reduce(session = session, pv = step.device, vgName = step.capability.volumeGroupName)
				LvmVg.list(session = session, vgName = step.capability.volumeGroupName).single().freeSize
			}

	override fun update(step: RemoveDiskFromVG, updates: BigInteger) {
		hostDao.update(step.host.id) { host ->
			host.copy(
					capabilities = host.capabilities!!.copy(
							storageCapabilities = host.capabilities.storageCapabilities.map {
								if (it is LvmStorageCapability && it.id == step.capability.id) {
									it.copy(
											physicalVolumes = it.physicalVolumes - step.device,
											size = updates
									)
								} else it
							}
					)
			)
		}
	}
}