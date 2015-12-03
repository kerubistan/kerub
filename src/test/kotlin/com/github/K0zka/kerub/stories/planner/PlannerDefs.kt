package com.github.K0zka.kerub.stories.planner

import com.github.K0zka.kerub.model.ExpectationLevel
import com.github.K0zka.kerub.model.Host
import com.github.K0zka.kerub.model.HostCapabilities
import com.github.K0zka.kerub.model.OperatingSystem
import com.github.K0zka.kerub.model.Range
import com.github.K0zka.kerub.model.VirtualMachine
import com.github.K0zka.kerub.model.VirtualMachineStatus
import com.github.K0zka.kerub.model.VirtualStorageDevice
import com.github.K0zka.kerub.model.VirtualStorageLink
import com.github.K0zka.kerub.model.dynamic.HostDynamic
import com.github.K0zka.kerub.model.dynamic.HostStatus
import com.github.K0zka.kerub.model.dynamic.VirtualMachineDynamic
import com.github.K0zka.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.K0zka.kerub.model.expectations.CacheSizeExpectation
import com.github.K0zka.kerub.model.expectations.ChassisManufacturerExpectation
import com.github.K0zka.kerub.model.expectations.CpuArchitectureExpectation
import com.github.K0zka.kerub.model.expectations.NotSameHostExpectation
import com.github.K0zka.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.K0zka.kerub.model.hardware.CacheInformation
import com.github.K0zka.kerub.model.hardware.ChassisInformation
import com.github.K0zka.kerub.model.hardware.ProcessorInformation
import com.github.K0zka.kerub.model.io.BusType
import com.github.K0zka.kerub.model.messages.EntityUpdateMessage
import com.github.K0zka.kerub.planner.OperationalState
import com.github.K0zka.kerub.planner.OperationalStateBuilder
import com.github.K0zka.kerub.planner.Plan
import com.github.K0zka.kerub.planner.PlanExecutor
import com.github.K0zka.kerub.planner.Planner
import com.github.K0zka.kerub.planner.PlannerImpl
import com.github.K0zka.kerub.planner.steps.host.startup.WakeHost
import com.github.K0zka.kerub.planner.steps.replace
import com.github.K0zka.kerub.planner.steps.vm.migrate.MigrateVirtualMachine
import com.github.K0zka.kerub.planner.steps.vm.start.StartVirtualMachine
import com.github.K0zka.kerub.planner.steps.vstorage.create.CreateImage
import com.github.K0zka.kerub.utils.toSize
import com.github.k0zka.finder4j.backtrack.BacktrackService
import cucumber.api.DataTable
import cucumber.api.PendingException
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import org.junit.Assert
import org.mockito.Matchers
import org.mockito.Mockito
import java.math.BigInteger
import kotlin.math.minus
import kotlin.math.plus

public class PlannerDefs {

	var vms = listOf<VirtualMachine>()
	var hosts = listOf<Host>()
	var vdisks = listOf<VirtualStorageDevice>()

	var vmDyns = listOf<VirtualMachineDynamic>()
	var hostDyns = listOf<HostDynamic>()
	var vstorageDyns = listOf<VirtualStorageDeviceDynamic>()

	val backtrack: BacktrackService = BacktrackService(1)
	val executor: PlanExecutor = Mockito.mock(PlanExecutor::class.java)
	val builder: OperationalStateBuilder = Mockito.mock(OperationalStateBuilder::class.java)

	val planner: Planner = PlannerImpl(
			backtrack,
			executor,
			builder
	)

	var executedPlans = listOf<Plan>()

	@Before
	fun setup() {
		Mockito.`when`(builder.buildState()).then {
			OperationalState.fromLists(
					hosts = hosts,
					hostDyns = hostDyns,
					vms = vms,
					vmDyns = vmDyns,
					vStorage = vdisks,
					vStorageDyns = vstorageDyns
			)
		}
		Mockito.doAnswer({
			executedPlans += (it.getArguments()[0] as Plan)
			Unit
		}).`when`(executor).execute(Matchers.any(Plan::class.java) ?: Plan(OperationalState()))
	}

	@Given("^VMs:$")
	fun setVms(vmsTable: DataTable) {

		val mb = 1024 * 1024

		val raw = vmsTable.raw()
		for (row in raw.filter { it != raw.first() }) {
			val vm = VirtualMachine(
					name = row[0],
					memory = Range<BigInteger>(
							min = (row[1].toSize()),
							max = (row[2].toSize())
					),
					nrOfCpus = row[3].toInt(),
					expectations = listOf(
							CpuArchitectureExpectation(
									cpuArchitecture = row[4]
							)
					)
			)
			this.vms = this.vms + vm
		}
	}

	@Given("^hosts:$")
	fun hosts(hostsTable: DataTable) {
		for (row in hostsTable.raw().filter { it != hostsTable.raw().first() }) {
			val host = Host(
					address = row[0],
					dedicated = true,
					publicKey = "",
					capabilities = HostCapabilities(
							os = OperatingSystem.Linux,
							cpuArchitecture = row[4],
							cpus = listOf(ProcessorInformation(
									manufacturer = "Test",
									coreCount = row[2].toInt(),
									threadCount = row[3].toInt(),
									flags = listOf(),
									l1cache = null,
									l2cache = null,
									l3cache = null,
									maxSpeedMhz = 3200,
									socket = "",
									version = "",
									voltage = null
							)
							),
							chassis = null,
							distribution = null,
							devices = listOf(),
							installedSoftware = listOf(),
							system = null,
							totalMemory = row[1].toSize()
					)
			)
			hosts += host
		}
	}

	@Given("(\\S+) is running on (\\S+)")
	fun setVmRunningOnHost(vmName: String, hostAddr: String) {
		val vm = vms.first { it.name == vmName }!!
		vms = vms.replace({ it.id == vm.id }, {
			it.copy(
					expectations = vm.expectations + listOf(
							VirtualMachineAvailabilityExpectation(up = true))
			)
		})
		val host = hosts.first { it.address == hostAddr }

		vmDyns = vmDyns + VirtualMachineDynamic(
				id = vm.id,
				hostId = host.id,
				status = VirtualMachineStatus.Up,
				lastUpdated = System.currentTimeMillis(),
				memoryUsed = vm.memory.min
		)

		requireNotNull(hostDyns.firstOrNull { it.id == host.id }, { "host must be up, otherwise no vm!" })

		hostDyns = hostDyns.replace({ it.id == host.id }, {
			it.copy(
					memFree = requireNotNull(it.memFree ?: host.capabilities?.totalMemory) - vm.memory.min,
					memUsed = (it.memUsed ?: BigInteger.ZERO) + vm.memory.min
			)
		})
	}

	@When("^VM (\\S+) is started$")
	fun startVm(vm: String) {
		vms = vms.map {
			if (it.name == vm) {
				it.copy(expectations = (
						it.expectations
								+ VirtualMachineAvailabilityExpectation(
								level = ExpectationLevel.DealBreaker,
								up = true
						)
						)
				)
			} else {
				it
			}
		}
		planner.onEvent(EntityUpdateMessage(
				obj = vms.filter { it.name == vm }.first(),
				date = System.currentTimeMillis()
		));
	}

	@Then("^VM (\\S+) gets scheduled on host (\\S+)$")
	fun verifyVmScheduledOnHost(vmName: String, hostAddress: String) {
		Assert.assertTrue(executedPlans.any {
			it.steps.any {
				it is StartVirtualMachine
						&& it.host.address == hostAddress
						&& it.vm.name == vmName
			}
		})
	}

	@Then("^(\\S+) will be migrated to (\\S+) as step (\\d+)")
	fun verifyVmMigration(vmName: String, targetHostAddr: String, stepNo: Int) {
		val migrationStep = executedPlans.first().steps.get(stepNo - 1)
		Assert.assertTrue(migrationStep is MigrateVirtualMachine)
		Assert.assertEquals((migrationStep as MigrateVirtualMachine).target.address, targetHostAddr)
		Assert.assertEquals(migrationStep.vm.name, vmName)
	}

	@Then("^VM (\\S+) gets scheduled on host (\\S+) as step (\\d+)$")
	fun verifyVmGetsScheduled(vmName: String, targetHostAddr: String, stepNo: Int) {
		val startStep = executedPlans.first().steps.get(stepNo - 1)
		Assert.assertTrue(startStep is StartVirtualMachine)
		Assert.assertEquals((startStep as StartVirtualMachine).host.address, targetHostAddr)
		Assert.assertEquals(startStep.vm.name, vmName)
	}

	@When("^optimization is triggered$")
	fun optimization_is_triggered() {
		throw PendingException();
	}

	@Then("^host (\\S+) should be go to power-save$")
	fun host_should_be_go_to_power_save(host: String) {
		throw PendingException();
	}

	@Given("^status:$")
	fun status(vmstatus: DataTable) {
		throw PendingException();
	}

	@Given("^host (\\S+) is Up$")
	fun setHostDyn(address: String) {
		var host = hosts.firstOrNull { it.address == address }!!
		hostDyns = hostDyns + HostDynamic(
				id = host.id,
				status = HostStatus.Up,
				memFree = host.capabilities?.totalMemory
		)

	}

	@Given("^host (\\S+) is Down$")
	fun setHostDown(hostAddress: String) {
		val host = hosts.first { host ->
			host.address == hostAddress
		}
		hostDyns = hostDyns.filter {
			dyn ->
			dyn.id != host.id
		}
	}

	@Then("^(\\S+) will be started as step (\\d+)$")
	fun verifyHostStartedUp(hostAddr: String, stepNo: Int) {
		val startStep = executedPlans.first().steps.get(stepNo - 1)
		Assert.assertTrue(startStep is WakeHost)
		Assert.assertEquals((startStep as WakeHost).host.address, hostAddr)
	}

	@Given("^virtual storage devices:$")
	fun setVirtualStorageDevices(vstorage: DataTable) {
		vdisks = vstorage.raw().filterNot { it[0] == "name" }.map {
			VirtualStorageDevice(
					name = it[0],
					size = it[1].toSize(),
					readOnly = it[2].toBoolean()
			)
		}
	}

	@Given("^(\\S+) is attached to (\\S+)$")
	fun attachDiskToVm(storageName: String, vmName: String) {
		val storage = vdisks.first { it.name == storageName }
		vms = vms.replace({ it.name == vmName }, {
			it.copy(
					virtualStorageLinks = it.virtualStorageLinks
							+ VirtualStorageLink(
							virtualStorageId = storage.id,
							bus = BusType.sata
					)
			)
		})
	}

	@Given("^(\\S+) is not yet created$")
	fun removeVStorageDyn(storageName: String) {
		val storage = vdisks.first { it.name == storageName }
		vstorageDyns = vstorageDyns.filterNot { it.id == storage.id }
	}

	@Given("^the virtual disk (\\S+) is created on (\\S+)$")
	fun createVStorageDyn(storageName: String, hostAddr: String) {
		val storage = vdisks.first { it.name == storageName }
		val host = hosts.first { it.address == hostAddr }
		vstorageDyns = vstorageDyns + VirtualStorageDeviceDynamic(
				id = storage.id,
				actualSize = storage.size,
				hostId = host.id
		)
	}

	@Then("^the virtual disk (\\S+) must be allocated on (\\S+)$")
	fun verifyVirtualStorageCreated(storageName: String, hostAddr: String) {
		val storage = vdisks.first { it.name == storageName }
		val host = hosts.first { it.address == hostAddr }
		Assert.assertTrue(executedPlans.first().steps.any { it is CreateImage && it.device == storage && it.host == host })
	}

	@Then("^the virtual disk (\\S+) must not be allocated$")
	fun verifyNoStorageCreate(storageName: String) {
		val storage = vdisks.first { it.name == storageName }
		Assert.assertTrue(executedPlans.first().steps.none { it is CreateImage && it.device == storage })

	}

	@Given("^no virtual machines$")
	fun clearVirtualMachines() {
		vms = listOf()
		vmDyns = listOf()
	}

	@Given("(\\S+) manufaturer has NO L(\\d) cache")
	fun setNoCache(hostAddress: String, cachelevel: Int) {
		hosts = hosts.replace({ it.address == hostAddress }, {
			host ->
			host.copy(
					capabilities = host.capabilities!!.copy(
							cpus = host.capabilities!!.cpus.replace({ true }, {
								cpu ->
								cpu.copy(
										l1cache = if (cachelevel == 1) null else cpu.l1cache,
										l2cache = if (cachelevel == 2) null else cpu.l2cache
								)
							})
					)
			)
		})
	}

	@Given("(\\S+) manufaturer has (\\S+\\s+\\S+) L(\\d) cache")
	fun setCacheSize(hostAddress: String, amount: String, cachelevel: Int) {
		val size = amount.toSize()
		hosts = hosts.replace({ it.address == hostAddress }, {
			host ->
			host.copy(
					capabilities = host.capabilities!!.copy(
							cpus = host.capabilities!!.cpus.replace({ true }, {
								cpu ->
								val cacheInformation = CacheInformation(
										size = size.toInt(),
										errorCorrection = "",
										operation = "",
										socket = "",
										speedNs = null
								)
								cpu.copy(
										l1cache = if (cachelevel == 1) cacheInformation else cpu.l1cache,
										l2cache = if (cachelevel == 2) cacheInformation else cpu.l2cache
								)
							})
					)
			)
		})
	}

	@Given("^VM (\\S+) requires (\\S+\\s+\\S+) L1 cache$")
	fun setVmCacheRequirement(vmName: String, amount: String) {
		vms = vms.replace({ it.name == vmName }, {
			vm ->
			vm.copy(
					expectations = vm.expectations + CacheSizeExpectation(
							minL1 = amount.toSize().toLong(),
							level = ExpectationLevel.DealBreaker
					)
			)
		})
	}

	@Given("^(\\S+) manufaturer is (\\S+)$")
	fun setHostManufacturer(hostAddr: String, manufacturer: String) {
		hosts = hosts.replace({ it.address == hostAddr }, {
			host ->
			host.copy(
					capabilities = host.capabilities?.copy(
							chassis = host.capabilities?.chassis?.copy(manufacturer = manufacturer) ?:
									ChassisInformation(
											manufacturer = manufacturer,
											type = "BLADE",
											height = null,
											nrOfPowerCords = 1
									)
					)
			)
		})
	}

	@Given("^VM (\\S+) requires manufacturer (\\S+)$")
	fun setVmHostManufacturerInformation(vmName : String, manufacturer : String) {
		vms = vms.replace( {it.name == vmName} , {
			vm ->
			vm.copy(
					expectations = vm.expectations + ChassisManufacturerExpectation(
							manufacturer = manufacturer
					)
			)
		})
	}

	@Given("^(\\S+) has notsame host expectation against (\\S+)$")
	fun addNotSameHostExpectation(vmName : String, otherVm: String) {
		val otherVm = vms.first { it.name == otherVm }
		vms = vms.replace({it.name == vmName}, {
			vm ->
			vm.copy(
					expectations = vm.expectations + NotSameHostExpectation(
							otherVmIds = listOf(otherVm.id)
					)
			)
		})

	}

	@Given("^(\\S+) has ECC memory$")
	fun setHostEccMemory(hostAddr: String) {
		throw PendingException()
	}

	@Given("^(\\S+) does not have ECC memory$")
	fun setHostNonEccMemory(hostAddr: String) {
		throw PendingException()
	}
}
