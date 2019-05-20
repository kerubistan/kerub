package com.github.kerubistan.kerub.planner

import com.github.k0zka.finder4j.backtrack.StepFactory
import com.github.kerubistan.kerub.hostUp
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.OperatingSystem
import com.github.kerubistan.kerub.model.SoftwarePackage.Companion.pack
import com.github.kerubistan.kerub.model.VirtualStorageLink
import com.github.kerubistan.kerub.model.VirtualStorageLinkInfo
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.collection.VirtualStorageDataCollection
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.SimpleStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.expectations.VirtualMachineAvailabilityExpectation
import com.github.kerubistan.kerub.model.hardware.BlockDevice
import com.github.kerubistan.kerub.model.io.BusType
import com.github.kerubistan.kerub.model.io.DeviceType
import com.github.kerubistan.kerub.planner.issues.problems.CompositeProblemDetectorImpl
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStep
import com.github.kerubistan.kerub.planner.steps.CompositeStepFactory
import com.github.kerubistan.kerub.planner.steps.storage.lvm.create.CreateLv
import com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.create.CreateLvmPool
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StartNfsDaemon
import com.github.kerubistan.kerub.planner.steps.storage.share.nfs.daemon.StopNfsDaemon
import com.github.kerubistan.kerub.planner.steps.vm.start.kvm.KvmStartVirtualMachine
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testVm
import com.github.kerubistan.kerub.utils.junix.common.Centos
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtArch
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtCapabilities
import com.github.kerubistan.kerub.utils.junix.virt.virsh.LibvirtGuest
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.kerubistan.kroki.size.GB
import io.github.kerubistan.kroki.size.TB
import org.junit.Test
import java.util.UUID.randomUUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlanRationalizerImplTest {

	@Test
	fun tryRemoveSingles() {
		val lvmStorageCapability = LvmStorageCapability(
				id = randomUUID(),
				volumeGroupName = "kerub-storage",
				size = 2.TB,
				physicalVolumes = mapOf("/dev/sda" to 1.TB, "/dev/sdb" to 1.TB)
		)
		val host = testHost.copy(
				capabilities = testHostCapabilities.copy(
						storageCapabilities = listOf(
								lvmStorageCapability
						),
						blockDevices = listOf(
								BlockDevice(deviceName = "/dev/sda", storageCapacity = 1.TB),
								BlockDevice(deviceName = "/dev/sdb", storageCapacity = 1.TB)
						),
						distribution = pack(Centos, "7"),
						installedSoftware = listOf(
								pack("qemu-kvm", "1.2.3"),
								pack("libvirt-client", "1.2.3")
						),
						hypervisorCapabilities = listOf(
								LibvirtCapabilities(
										guests = listOf(
												LibvirtGuest(
														arch = LibvirtArch(
																name = "X86_64",
																emulator = "qemu-kvm",
																wordsize = 64), osType = "any")
										)
								)
						),
						os = OperatingSystem.Linux
				)
		)
		val vm = testVm.copy(
				virtualStorageLinks = listOf(
						VirtualStorageLink(
								device = DeviceType.disk,
								bus = BusType.sata,
								virtualStorageId = testDisk.id
						)
				),
				expectations = listOf(
						VirtualMachineAvailabilityExpectation()
				),
				architecture = "X86_64"
		)
		val allocation = VirtualStorageLvmAllocation(
				hostId = host.id,
				vgName = lvmStorageCapability.volumeGroupName,
				actualSize = testDisk.size,
				capabilityId = lvmStorageCapability.id,
				path = "/dev/kerub-storage/${testDisk.id}"
		)
		val hostDyn = hostUp(host).copy(
				storageStatus = listOf(
						SimpleStorageDeviceDynamic(
								id = lvmStorageCapability.id,
								freeCapacity = lvmStorageCapability.size
						)
				),
				memFree = 4.GB
		)
		val plan = Plan(
				state = OperationalState.fromLists(
						hosts = listOf(host),
						hostDyns = listOf(hostDyn),
						vms = listOf(vm),
						vmDyns = listOf(),
						vStorage = listOf(testDisk),
						vStorageDyns = listOf()
				),
				steps = listOf(
						CreateLv(
								host = host,
								capability = lvmStorageCapability,
								disk = testDisk
						),
						// this step is really not needed
						// as the testDisk was already created by the previous CreateLv step
						CreateLvmPool(
								host = host,
								size = 100.GB,
								name = randomUUID().toString(),
								vgName = lvmStorageCapability.volumeGroupName
						),
						KvmStartVirtualMachine(
								host = host,
								vm = vm,
								storageLinks = listOf(
										VirtualStorageLinkInfo(
												link = vm.virtualStorageLinks.single(),
												allocation = allocation,
												storageHost = HostDataCollection(
														stat = host,
														dynamic = hostDyn,
														config = null
												),
												device = VirtualStorageDataCollection(
														stat = testDisk,
														dynamic = VirtualStorageDeviceDynamic(
																id = testDisk.id,
																allocations = listOf(allocation)
														)
												),
												hostServiceUsed = null
										)
								)
						)
				)
		)
		val rationalizedPlan = PlanRationalizerImpl(
				stepFactory = CompositeStepFactory(
						planViolationDetector = PlanViolationDetectorImpl,
						problemDetector = CompositeProblemDetectorImpl
				),
				violationDetector = PlanViolationDetectorImpl,
				problemDetector = CompositeProblemDetectorImpl
		).tryRemoveSingles(plan)
		assertEquals(2, rationalizedPlan.steps.size, "2 steps should be enough")
		assertEquals(
				CreateLv::class.java.name,
				rationalizedPlan.steps.first().javaClass.name,
				"first step should be createlv")
		assertEquals(
				KvmStartVirtualMachine::class.java.name,
				rationalizedPlan.steps[1].javaClass.name,
				"second step should be kvm start")
	}

	@Test
	fun tryRemoveInverses() {
		val vm = testVm.copy(expectations = listOf(VirtualMachineAvailabilityExpectation()))
		val startVirtualMachine = KvmStartVirtualMachine(vm = vm, host = testHost)
		assertTrue("remove the fully irrelevant steps from after and before") {
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							StartNfsDaemon(host = testHost),
							startVirtualMachine,
							StopNfsDaemon(host = testHost)
					)
			)

			val rational = PlanRationalizerImpl(mock(), mock(), mock()).tryRemoveInverses(plan)
			rational.steps == listOf(startVirtualMachine)
		}

		assertTrue("remove the fully irrelevant steps from before") {
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							StartNfsDaemon(host = testHost),
							StopNfsDaemon(host = testHost),
							startVirtualMachine
					)
			)

			val rational = PlanRationalizerImpl(mock(), mock(), mock()).tryRemoveInverses(plan)
			rational.steps == listOf(startVirtualMachine)
		}

		assertTrue("remove the fully irrelevant steps from after") {
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							startVirtualMachine,
							StartNfsDaemon(host = testHost),
							StopNfsDaemon(host = testHost)
					)
			)

			val rational = PlanRationalizerImpl(mock(), mock(), mock()).tryRemoveInverses(plan)
			rational.steps == listOf(startVirtualMachine)
		}

	}

	@Test
	fun rationalize() {
		assertTrue("there is not much left to rationalize on a blank plan") {
			val blank = Plan(
					states = listOf(),
					steps = listOf()
			)
			PlanRationalizerImpl(mock(), mock(), mock()).rationalize(blank) == blank
		}

		assertTrue("there is nothing to rationalize on a single step solution either") {
			val vm = testVm.copy(expectations = listOf(VirtualMachineAvailabilityExpectation()))
			val onestep = Plan(
					states = listOf(
							OperationalState.fromLists(
									hosts = listOf(testHost),
									hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
									vms = listOf(vm)
							)
					),
					steps = listOf(KvmStartVirtualMachine(vm = vm, host = testHost))
			)
			PlanRationalizerImpl(mock(), mock(), mock()).rationalize(onestep) == onestep
		}

		assertTrue("remove totally pointless step to start nfs server") {
			val stepFactory = mock<StepFactory<AbstractOperationalStep, Plan>>()
			val vm = testVm.copy(expectations = listOf(VirtualMachineAvailabilityExpectation()))
			val startVirtualMachine = KvmStartVirtualMachine(vm = vm, host = testHost)
			whenever(stepFactory.produce(any())).thenReturn(listOf(startVirtualMachine))
			val plan = Plan.planBy(
					initial = OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vms = listOf(vm)
					),
					steps = listOf(
							StartNfsDaemon(host = testHost),
							startVirtualMachine,
							StopNfsDaemon(host = testHost)
					)
			)
			PlanRationalizerImpl(stepFactory, mock(), mock()).rationalize(plan).steps == listOf(
					KvmStartVirtualMachine(vm = vm, host = testHost)
			)
		}

	}
}