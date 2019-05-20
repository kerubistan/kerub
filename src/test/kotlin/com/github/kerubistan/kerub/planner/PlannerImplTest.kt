package com.github.kerubistan.kerub.planner

import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.Range
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.planner.reservations.UseHostReservation
import com.github.kerubistan.kerub.planner.reservations.VmReservation
import com.github.kerubistan.kerub.utils.toSize
import io.github.kerubistan.kroki.size.GB
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerImplTest {

	val vm1 = VirtualMachine(
			name = "vm1",
			nrOfCpus = 1,
			memory = Range("512 MB".toSize(), 1.GB)
	)

	val vm2 = VirtualMachine(
			name = "vm2",
			nrOfCpus = 1,
			memory = Range("512 MB".toSize(), 1.GB)
	)

	val host1 = Host(
			address = "host1.example.com",
			publicKey = "",
			dedicated = true
	)

	val host2 = Host(
			address = "host2.example.com",
			publicKey = "",
			dedicated = true
	)

	@Test
	fun checkReservations() {
		assertTrue(PlannerImpl.checkReservations(listOf(), listOf(), OperationalState.fromLists()))
		assertTrue(PlannerImpl.checkReservations(listOf(VmReservation(vm1)), listOf(), OperationalState.fromLists()))
		assertTrue(
				PlannerImpl.checkReservations(
						listOf(VmReservation(vm1)),
						listOf(VmReservation(vm2)),
						OperationalState.fromLists()
				)
		)
		assertFalse(
				PlannerImpl.checkReservations(
						listOf(VmReservation(vm1)),
						listOf(VmReservation(vm1)),
						OperationalState.fromLists()
				)
		)
	}

	@Test
	fun checkReservation() {
		assertTrue(PlannerImpl.checkReservation(VmReservation(vm1), listOf(), OperationalState.fromLists()))
		assertTrue(
				PlannerImpl.checkReservation(
						VmReservation(vm1),
						listOf(VmReservation(vm2)),
						OperationalState.fromLists()
				)
		)
		assertFalse(
				PlannerImpl.checkReservation(
						VmReservation(vm1),
						listOf(VmReservation(vm1)),
						OperationalState.fromLists()
				)
		)
		assertTrue(
				PlannerImpl.checkReservation(
						VmReservation(vm1),
						listOf(UseHostReservation(host1)),
						OperationalState.fromLists()
				)
		)
		assertFalse(
				PlannerImpl.checkReservation(
						UseHostReservation(host1),
						listOf(UseHostReservation(host1)),
						OperationalState.fromLists()
				)
		)
		assertTrue(
				PlannerImpl.checkReservation(
						UseHostReservation(host1),
						listOf(UseHostReservation(host2)),
						OperationalState.fromLists()
				)
		)
	}
}
