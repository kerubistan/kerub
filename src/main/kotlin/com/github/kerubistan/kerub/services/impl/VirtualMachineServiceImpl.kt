package com.github.kerubistan.kerub.services.impl

import com.github.kerubistan.kerub.data.VirtualMachineDao
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.security.AssetAccessController
import com.github.kerubistan.kerub.services.VirtualMachineService
import java.util.UUID

class VirtualMachineServiceImpl(
		dao: VirtualMachineDao,
		accessController: AssetAccessController
) : AbstractAssetService<VirtualMachine>(accessController, dao, "vm"),
		VirtualMachineService {

	override fun startVm(id: UUID) {
		doWithVm(id, {
			vm ->
			alterAvailabilityExpectations(VirtualMachineAvailabilityExpectation(up = true), vm)
		})
	}

	override fun stopVm(id: UUID) {
		doWithVm(id, {
			vm ->
			alterAvailabilityExpectations(VirtualMachineAvailabilityExpectation(up = false), vm)
		})
	}

	override fun listByVirtualDisk(virtualDiskId: UUID): List<VirtualMachine> =
			accessController.filter((dao as VirtualMachineDao).listByAttachedStorage(virtualDiskId).map { accessController.checkAndDo(it) { it }!! })

	private fun alterAvailabilityExpectations(newExpectation: VirtualMachineAvailabilityExpectation, vm: VirtualMachine): VirtualMachine {
		return vm.copy(
				expectations = vm.expectations
						.filterNot { it is VirtualMachineAvailabilityExpectation }
						+ newExpectation
		)
	}

	internal fun doWithVm(id: UUID, action: (VirtualMachine) -> VirtualMachine) {
		update(id, action(getById(id)))
	}


}