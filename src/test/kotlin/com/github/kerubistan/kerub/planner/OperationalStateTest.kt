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
	fun getNrOfUnsatisfiedExpectationsWithEmptyState() {
		Assert.assertEquals(OperationalState().getNrOfUnsatisfiedExpectations(ExpectationLevel.DealBreaker), 0)
		Assert.assertEquals(OperationalState().getNrOfUnsatisfiedExpectations(ExpectationLevel.Want), 0)
		Assert.assertEquals(OperationalState().getNrOfUnsatisfiedExpectations(ExpectationLevel.Wish), 0)
	}

	@Test
	fun getNrOfUnsatisfiedExpectationsWithSingleVmSingleDealBreaker() {
		val state = OperationalState.fromLists(
				vms = listOf(
						VirtualMachine(
								id = randomUUID(),
								name = "test-vm",
								expectations = listOf(
										VirtualMachineAvailabilityExpectation(up = true, level = ExpectationLevel.DealBreaker)
								)
						)
				)
		)


		Assert.assertEquals(state.getNrOfUnsatisfiedExpectations(ExpectationLevel.DealBreaker), 1)
		Assert.assertEquals(state.getNrOfUnsatisfiedExpectations(ExpectationLevel.Want), 0)
		Assert.assertEquals(state.getNrOfUnsatisfiedExpectations(ExpectationLevel.Wish), 0)

	}

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

	@Test
	fun isExpectationSatisfiedWithCpuDedicationExpectation() {
		//under-utilization
		assertTrue {
			val exp = CoreDedicationExpectation()
			val vm = testVm.copy(
					expectations = listOf(exp),
					nrOfCpus = 1
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(testProcessor)
					)
			)
			OperationalState.fromLists(
					hosts = listOf(
							host
					),
					hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
					vms = listOf(vm),
					vmDyns = listOf(
							VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									hostId = host.id,
									memoryUsed = "1 GB".toSize()
							)
					)
			).isExpectationSatisfied(expectation = exp, vm = vm)
		}
		assertFalse {
			val exp = CoreDedicationExpectation()
			val vm = testVm.copy(
					expectations = listOf(exp),
					nrOfCpus = 4 // more vcpu allocated...
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(testProcessor.copy(coreCount = 2)) // ...than what the host have
					)
			)
			OperationalState.fromLists(
					hosts = listOf(
							host
					),
					hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
					vms = listOf(vm),
					vmDyns = listOf(
							VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									hostId = host.id,
									memoryUsed = "1 GB".toSize()
							)
					)
			).isExpectationSatisfied(expectation = exp, vm = vm)
		}
		assertFalse {
			val exp = CoreDedicationExpectation()
			val vm = testVm.copy(
					id = randomUUID(),
					expectations = listOf(exp),
					nrOfCpus = 2 // two dedicated
			)

			val nonDedicated = testVm.copy(
					id = randomUUID(),
					expectations = listOf(),
					nrOfCpus = 2 // and two non-dedicated
			)
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							cpus = listOf(testProcessor.copy(coreCount = 2)) // for only two cores
					)
			)
			OperationalState.fromLists(
					hosts = listOf(
							host
					),
					hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
					vms = listOf(vm, nonDedicated),
					vmDyns = listOf(
							VirtualMachineDynamic(
									id = vm.id,
									status = VirtualMachineStatus.Up,
									hostId = host.id,
									memoryUsed = "1 GB".toSize()
							),
							VirtualMachineDynamic(
									id = nonDedicated.id,
									status = VirtualMachineStatus.Up,
									hostId = host.id,
									memoryUsed = "1 GB".toSize()
							)
					)
			).isExpectationSatisfied(expectation = exp, vm = vm)
		}
	}

	@Test
	fun getUnsatisfiedExpectations() {
		val host1id = randomUUID()
		OperationalState.fromLists(
				hosts = listOf(
						testHost.copy(
								id = host1id,
								address = "trashcan.example.com",
								recycling = true
						)
				),
				vms = listOf(
						testVm
				),
				vmDyns = listOf(
						VirtualMachineDynamic(
								id = testVm.id,
								status = VirtualMachineStatus.Up,
								hostId = host1id,
								memoryUsed = "1 GB".toSize()
						)
				)
		).let {
			assertTrue(it.getUnsatisfiedExpectations().any { it is WorkingHostExpectation })
		}
	}

}