package com.github.K0zka.kerub.services.impl

import com.github.K0zka.kerub.data.VirtualMachineDao
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.paging.SearchResultPage
import com.github.K0zka.kerub.model.paging.SortResultPage
import com.github.K0zka.kerub.security.AccessController
import com.github.K0zka.kerub.services.VirtualMachineService
import java.util.UUID

class VirtualMachineServiceImpl(
		dao: VirtualMachineDao,
		accessController: AccessController
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

	override fun search(field: String, value: String, start: Long, limit: Int): SearchResultPage<VirtualMachine> {
		val list = (dao as VirtualMachineDao).fieldSearch(field, value, start, limit)
		return SearchResultPage(
				start = start.toLong(),
				count = list.size.toLong(),
				result = list,
				total = dao.count().toLong(),
				searchby = field
		)
	}

}