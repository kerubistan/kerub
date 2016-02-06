package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.services.VirtualMachineService
import java.util.UUID

class VirtualMachineServiceImpl(dao: VirtualMachineDao) : ListableBaseService<VirtualMachine>(dao, "vm"),
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