package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.ExpectationLevel
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualMachineStatus
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.WorkingHostExpectation
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualMachineDynamic
import com.github.kerubistan.kerub.model.expectations.CoreDedicationExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.planner.reservations.VirtualStorageReservation
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testProcessor
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OperationalStateTest {

	@Test
	fun virtualStorageToCheck() {
		val vDiskNotPlanned = VirtualStorageDevice(
				id = randomUUID(),
				name = "not-planned",
				size = "20 GB".toSize()
		)
		val vDiskPlanned = VirtualStorageDevice(
				id = randomUUID(),
				name = "planned",
				size = "20 GB".toSize()
		)
		val state = OperationalState.fromLists(
				vStorage = listOf(vDiskNotPlanned, vDiskPlanned),
				reservations = listOf(VirtualStorageReservation(vDiskPlanned))
		)


		val list = state.virtualStorageToCheck()

		assertEquals(1, list.size)
		assert(list[0] == vDiskNotPlanned)
	}
}