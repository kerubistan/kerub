package com.github.K0zka.kerub.planner.steps.vstorage.gvinum.create

import com.github.K0zka.kerub.data.dynamic.VirtualStorageDeviceDynamicDao
import com.github.K0zka.kerub.host.HostCommandExecutor
import com.github.K0zka.kerub.model.dynamic.SimpleGvinumConfiguration
import com.github.K0zka.kerub.planner.StepExecutor
import com.github.K0zka.kerub.utils.junix.storagemanager.gvinum.GVinum

class CreateGvinumVolumeExecutor(
		private val hostCommandExecutor: HostCommandExecutor,
		private val virtualDiskDynDao: VirtualStorageDeviceDynamicDao
) : StepExecutor<CreateGvinumVolume> {
	override fun execute(step: CreateGvinumVolume) {
		hostCommandExecutor.execute(step.host) {
			session ->
			when (step.config) {
				is SimpleGvinumConfiguration -> {
					GVinum.createSimpleVolume(session = session, volName =  step.disk.id.toString(), disk = "", size = step.disk.size)
				}
				else -> {
					TODO("gvinum configuration not implemented by executor: ${step.config}")
				}
			}
		}
	}
}