package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.TemplateDao
import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.data.VirtualStorageDeviceDao
import com.github.kerubistan.kerub.data.dynamic.VirtualMachineDynamicDao
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.VirtualMachineService
import com.github.kerubistan.kerub.services.VmFromTemplateRequest
import java.util.UUID
import java.util.UUID.randomUUID

class VirtualMachineServiceImpl(
		dao: VirtualMachineDao,
		accessController: AssetAccessController,
		private val dynDao: VirtualMachineDynamicDao,
		private val templateDao: TemplateDao,
		private val virtualStorageDeviceDao: VirtualStorageDeviceDao
) : AbstractAssetService<VirtualMachine>(accessController, dao, "vm"),
		VirtualMachineService {

	override fun createFromTemplate(request: VmFromTemplateRequest) {
		val template = requireNotNull(accessController.doAndCheck { templateDao[request.templateId] }) {
			"template ${request.templateId} not found"
		}
		val vmId = randomUUID()
		val virtualStorageLinks = template.vm.virtualStorageLinks.map { link ->
			val storageDevice = requireNotNull(accessController.doAndCheck { virtualStorageDeviceDao[link.virtualStorageId] })
			val storageDeviceForVm = if (link.readOnly) {
				// not clone if it is read-only anyway, we can just use the same
				storageDevice
			} else {
				// a clone of the storage device if it is rw
				storageDevice.copy(
						expectations = storageDevice.expectations.filterNot {
							it is StorageAvailabilityExpectation
									|| it is CloneOfStorageExpectation
						} + CloneOfStorageExpectation(sourceStorageId = link.virtualStorageId),
						owner = request.owner,
						readOnly = false
				)
			}
			link.virtualStorageId to storageDeviceForVm
		}.toMap()
		virtualStorageDeviceDao.addAll(virtualStorageLinks.filterNot { it.value.readOnly }.values)
		val vm = template.vm.copy(
				name = request.vmName ?: template.vmNamePrefix + vmId,
				id = vmId,
				virtualStorageLinks = template.vm.virtualStorageLinks.map { link ->
					link.copy(
							virtualStorageId = requireNotNull(virtualStorageLinks[link.virtualStorageId]).id
					)
				}
		)
		dao.add(vm)
	}

	override fun startVm(id: UUID) {
		doWithVm(id) { vm ->
			alterAvailabilityExpectations(VirtualMachineAvailabilityExpectation(up = true), vm)
		}
	}

	override fun stopVm(id: UUID) {
		doWithVm(id) { vm ->
			alterAvailabilityExpectations(VirtualMachineAvailabilityExpectation(up = false), vm)
		}
	}

	override fun listByVirtualDisk(virtualDiskId: UUID): List<VirtualMachine> =
			accessController.filter((dao as VirtualMachineDao)
					.listByAttachedStorage(virtualDiskId)
					.map { accessController.checkAndDo(it) { it }!! })

	private fun alterAvailabilityExpectations(
			newExpectation: VirtualMachineAvailabilityExpectation, vm: VirtualMachine
	): VirtualMachine = vm.copy(
			expectations = vm.expectations
					.filterNot { it is VirtualMachineAvailabilityExpectation }
					+ newExpectation
	)

	internal fun doWithVm(id: UUID, action: (VirtualMachine) -> VirtualMachine) {
		update(id, action(getById(id)))
	}

	override fun beforeRemove(entity: VirtualMachine) {
		check(entity.expectations.none { it is VirtualMachineAvailabilityExpectation && it.up }) {
			"VM must be down"
		}
		check(dynDao[entity.id]?.let { it.status == VirtualMachineStatus.Down } ?: true) { "VM must be down" }
		super.beforeRemove(entity)
	}

}