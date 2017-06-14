package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.planner.reservations.UseHostReservation
import com.github.K0zka.kerub.planner.reservations.VmReservation
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerImplTest {

	val vm1 = VirtualMachine(
			name = "vm1",
			nrOfCpus = 1,
			memory = Range("512 MB".toSize(), "1 GB".toSize())
	)

	val vm2 = VirtualMachine(
			name = "vm2",
			nrOfCpus = 1,
			memory = Range("512 MB".toSize(), "1 GB".toSize())
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
		assertTrue(PlannerImpl.checkReservations(listOf(), listOf()))
		assertTrue(PlannerImpl.checkReservations(listOf(VmReservation(vm1)), listOf()))
		assertTrue(PlannerImpl.checkReservations(listOf(VmReservation(vm1)), listOf(VmReservation(vm2))))
		assertFalse(PlannerImpl.checkReservations(listOf(VmReservation(vm1)), listOf(VmReservation(vm1))))
	}

	@Test
	fun checkReservation() {
		assertTrue(PlannerImpl.checkReservation(VmReservation(vm1), listOf()))
		assertTrue(PlannerImpl.checkReservation(VmReservation(vm1), listOf(VmReservation(vm2))))
		assertFalse(PlannerImpl.checkReservation(VmReservation(vm1), listOf(VmReservation(vm1))))

		assertTrue(PlannerImpl.checkReservation(VmReservation(vm1), listOf(UseHostReservation(host1))))

		assertTrue(PlannerImpl.checkReservation(UseHostReservation(host1), listOf(UseHostReservation(host1))))
		assertTrue(PlannerImpl.checkReservation(UseHostReservation(host1), listOf(UseHostReservation(host2))))

	}
}
