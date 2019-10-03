package com.github.kerubistan.kerub.stories.plannerexecutor

import com.github.k0zka.finder4j.backtrack.BacktrackServiceImpl
import com.github.kerubistan.kerub.model.Host
import com.github.kerubistan.kerub.model.HostCapabilities
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version
import com.github.kerubistan.kerub.model.VirtualMachine
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualMachineDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.CompositeStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.hardware.ProcessorInformation
import com.github.kerubistan.kerub.model.hypervisor.LibvirtArch
import com.github.kerubistan.kerub.model.hypervisor.LibvirtCapabilities
import com.github.kerubistan.kerub.model.hypervisor.LibvirtGuest
import com.github.kerubistan.kerub.model.messages.EntityUpdateMessage
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.OperationalStateBuilder
import com.github.kerubistan.kerub.planner.Plan
import com.github.kerubistan.kerub.planner.PlanExecutor
import com.github.kerubistan.kerub.planner.PlanViolationDetectorImpl
import com.github.kerubistan.kerub.planner.Planner
import com.github.kerubistan.kerub.planner.PlannerImpl
import com.github.kerubistan.kerub.utils.getLogger
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.PB
import io.github.kerubistan.kroki.size.TB
import io.github.kerubistan.kroki.time.now
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals

class PlannerExecutorDefs {

	companion object {
		private val logger = getLogger(PlannerExecutorDefs::class)
	}

	class MockExecutor : PlanExecutor {
		var plans = listOf<Plan>()
		override fun execute(plan: Plan, callback: (Plan) -> Unit) {
			synchronized(this) {
				plans = plans + plan
			}
		}
	}

	private var planner: Planner = mock()
	private var executor: PlanExecutor = MockExecutor()
	private var stateBuilder: OperationalStateBuilder = mock()
	private var state = OperationalState.fromLists()

	private var host = Host(
			id = UUID.randomUUID(),
			address = "host-1.example.com",
			dedicated = true,
			publicKey = "",
			capabilities = HostCapabilities(
					os = OperatingSystem.Linux,
					totalMemory = 1.TB,
					distribution = SoftwarePackage(name = "CentOS Linux", version = Version.fromVersionString("7.0")),
					installedSoftware = listOf(
							SoftwarePackage(name = "qemu-kvm", version = Version.fromVersionString("2.4.1")),
							SoftwarePackage(name = "libvirt-client", version = Version.fromVersionString("1.2.18"))
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
									voltage = BigDecimal.ZERO,
									flags = listOf("vmx")
							)
					),
					storageCapabilities = listOf(
							LvmStorageCapability(
									id = UUID.randomUUID(),
									volumeGroupName = "TEST",
									size = 1.PB,
									physicalVolumes = mapOf("/dev/sda" to 1.PB))
					),
					hypervisorCapabilities = listOf(
							LibvirtCapabilities(
									guests = listOf(
											LibvirtGuest(
													osType = "hvm",
													arch = LibvirtArch(
															name = "x86_64",
															wordsize = 64,
															emulator = "/usr/bin/qemu-kvm"
													)
											)
									)
							)
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
		planner = PlannerImpl(BacktrackServiceImpl(), executor, stateBuilder, PlanViolationDetectorImpl)
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
				hosts = mapOf(host.id to HostDataCollection(stat = host, dynamic = HostDynamic(
						id = host.id,
						status = HostStatus.Up,
						memFree = 1.TB,
						storageStatus = listOf(
								CompositeStorageDeviceDynamic(
										id = host.capabilities!!.storageCapabilities[0].id,
										reportedFreeCapacity = 1.PB
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
				size = 100.GB,
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
							id = host.id
					),
					date = now())
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