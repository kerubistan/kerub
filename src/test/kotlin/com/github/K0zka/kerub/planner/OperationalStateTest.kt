package com.github.K0zka.kerub.planner

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.expectations.CoreDedicationExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.planner.reservations.VirtualStorageReservation
import com.github.K0zka.kerub.testHost
import com.github.K0zka.kerub.testHostCapabilities
import com.github.K0zka.kerub.testProcessor
import com.github.K0zka.kerub.testVm
import com.github.K0zka.kerub.utils.toSize
import org.junit.Assert
import org.junit.Test
import java.util.UUID
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
								id = UUID.randomUUID(),
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
				id = UUID.randomUUID(),
				name = "not-planned",
				size = "20 GB".toSize()
		)
		val vDiskPlanned = VirtualStorageDevice(
				id = UUID.randomUUID(),
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
					id = UUID.randomUUID(),
					expectations = listOf(exp),
					nrOfCpus = 2 // two dedicated
			)

			val nonDedicated = testVm.copy(
					id = UUID.randomUUID(),
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

}