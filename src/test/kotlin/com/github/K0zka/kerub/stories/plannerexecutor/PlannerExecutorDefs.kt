package com.github.K0zka.kerub.stories.plannerexecutor

import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.LvmStorageCapability
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.SoftwarePackage
import com.github.K0zka.kerub.model.Version
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.collection.HostDataCollection
import com.github.K0zka.kerub.model.collection.VirtualMachineDataCollection
import com.github.K0zka.kerub.model.collection.VirtualStorageDataCollection
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.StorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.OperationalStateBuilder
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.PlanExecutor
import com.github.K0zka.kerub.planner.Planner
import com.github.K0zka.kerub.planner.PlannerImpl
import com.github.K0zka.kerub.utils.getLogger
import com.github.K0zka.kerub.utils.toSize
import com.github.k0zka.finder4j.backtrack.BacktrackService
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals

class PlannerExecutorDefs {

	val logger = getLogger(PlannerExecutorDefs::class)

	class MockExecutor : PlanExecutor {
		var plans = listOf<Plan>()
		override fun execute(plan: Plan, callback: (Plan) -> Unit) {
			synchronized(this) {
				plans += plan
			}
		}
	}

	var planner: Planner = mock()
	var executor: PlanExecutor = MockExecutor()
	var stateBuilder: OperationalStateBuilder = mock()
	var state = OperationalState.fromLists()

	var host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = "",
			capabilities = HostCapabilities(
					os = OperatingSystem.Linux,
					totalMemory = "1 TB".toSize(),
					distribution = SoftwarePackage(name = "Centos", version = Version.fromVersionString("7.0")),
					installedSoftware = listOf(
							SoftwarePackage(name = "qemu-kvm", version = Version.fromVersionString("2.4.1")),
							SoftwarePackage(name = "libvirt", version = Version.fromVersionString("1.2.18"))
					),
					cpuArchitecture = "X86_64",
					cpus = listOf(
							ProcessorInformation(
									manufacturer = "Blah",
									coreCount = 4,
									threadCount = 8,
									socket = "",
									version = "",
									maxSpeedMhz = 3000,
									voltage = BigDecimal.ZERO
							)
					),
					storageCapabilities = listOf(
							LvmStorageCapability(
									id = UUID.randomUUID(),
									volumeGroupName = "TEST",
									size = "1 PB".toSize(),
									physicalVolumes = listOf("1 PB".toSize()))
					)
			)
	)

	@Before
	fun wireup() {
		whenever(stateBuilder.buildState()).then {
			logger.info("building state")
			state
		}
	}

	@Given("the planner")
	fun createPlanner() {
		planner = PlannerImpl(BacktrackService(), executor, stateBuilder)
	}

	@Given("a VM")
	fun addVmToSituation() {
		val vm = VirtualMachine(
				id = UUID.randomUUID(),
				name = "test-vm-1",
				expectations = listOf(),
				nrOfCpus = 1
		)
		state = state.copy(vms = mapOf(vm.id to VirtualMachineDataCollection(stat = vm, dynamic = null)))
	}

	@Given("a host")
	fun addHostToSituation() {
		state = state.copy(
				hosts = mapOf(host.id to /*host*/ HostDataCollection(stat = host, dynamic = HostDynamic(
						id = host.id,
						status = HostStatus.Up,
						memFree = "1 TB".toSize(),
						storageStatus = listOf(
								StorageDeviceDynamic(
										id = host.capabilities!!.storageCapabilities[0].id,
										freeCapacity = "1 PB".toSize()
								)
						)
				)))
		)
	}

	@Given("a disk")
	fun addVirtualDiskToSituation() {
		val disk = VirtualStorageDevice(
				id = UUID.randomUUID(),
				name = "test-disk",
				size = "100 GB".toSize(),
				expectations = listOf()
		)

		state = state.copy(vStorage = mapOf(disk.id to VirtualStorageDataCollection(stat = disk, dynamic = null)))
	}

	@Given("a dummy executor")
	fun createMockExecutor() {
		executor = MockExecutor()
	}

	@When("planner receives events")
	fun sendEventsToPlanner() {
		for (i in 1..10) {
			planner.onEvent(EntityUpdateMessage(
					obj = HostDynamic(
							id = host.id,
							cpuStats = listOf(),
							ksmEnabled = false
					),
					date = System.currentTimeMillis())
			)
		}
	}

	@When("the VM gets started")
	fun setVmAvailabilityRequirement() {
		state = state.copy(
				vms = state.vms.map {
					val vmData = it.value
					it.key to vmData.copy(
							stat = vmData.stat.copy(
									expectations = vmData.stat.expectations + VirtualMachineAvailabilityExpectation()
							)
					)
				}.toMap()
		)
	}

	@When("the disk needs allocation")
	fun setVStorageAllocationRequirement() {
		state = state.copy(
				vStorage = state.vStorage.map {
					val storageData = it.value
					it.key to storageData.copy(
							stat = storageData.stat.copy(
									expectations = storageData.stat.expectations + StorageAvailabilityExpectation()
							)
					)
				}.toMap()
		)
	}

	@Then("executor should receive exactly (\\d+) plan to execute")
	fun checkNrOfPlans(nr: Int) {
		assertEquals(nr, (executor as MockExecutor).plans.size)
	}
}