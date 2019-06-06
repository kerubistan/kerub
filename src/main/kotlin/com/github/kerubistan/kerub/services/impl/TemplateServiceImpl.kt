package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.TemplateDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.model.Template
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.TemplateService
import com.github.kerubistan.kerub.utils.byId
import java.util.UUID

class TemplateServiceImpl(
		dao: TemplateDao,
		private val vmDao: VirtualMachineDao,
		private val virtualStorageDeviceDao: VirtualStorageDeviceDao,
		accessController: AssetAccessController
) : TemplateService, AbstractAssetService<Template>(accessController, dao, "template") {
	override fun buildFromVm(vmId: UUID): Template {
		val vm = assertExist("vm", accessController.doAndCheck { vmDao[vmId] }, vmId)
		val storageDevices = virtualStorageDeviceDao.list(vm.virtualStorageLinks.map { it.virtualStorageId })
		val storageDeicesById = storageDevices.byId()

		val clonedStorageDevices = storageDevices.filterNot { it.readOnly }.map { sourceStorageDevice ->
			val newDevice = sourceStorageDevice.copy(
					id = UUID.randomUUID(),
					expectations = sourceStorageDevice.expectations
							+ CloneOfStorageExpectation(sourceStorageId = sourceStorageDevice.id)
			)
			sourceStorageDevice.id to newDevice
		}.toMap()
		virtualStorageDeviceDao.addAll(clonedStorageDevices.values)

		val templateVm = vm.copy(
				virtualStorageLinks = vm.virtualStorageLinks.map {
					val storage = requireNotNull(storageDeicesById[it.virtualStorageId])
					if (storage.readOnly) {
						it
					} else {
						it.copy(
								virtualStorageId = requireNotNull(clonedStorageDevices[it.virtualStorageId]).id
						)
					}
				}
		)
		val template = Template(
				vm = templateVm,
				name = "template from ${vm.name}",
				vmNamePrefix = ""
		)
		dao.add(template)
		return template
	}
}