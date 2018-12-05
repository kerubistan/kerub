package com.github.kerubistan.kerub.planner.steps.vstorage.share.iscsi.ctld

import com.github.kerubistan.kerub.GB
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import org.junit.Test
import kotlin.test.assertTrue

class CtldIscsiShareFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state should produce no steps") {
			CtldIscsiShareFactory.produce(OperationalState.fromLists()).isEmpty()
		}
		assertTrue("single shared - no steps") {
			CtldIscsiShareFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testFreeBsdHost),
							hostDyns = listOf(
									HostDynamic(id = testFreeBsdHost.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testFreeBsdHost.id,
											services = listOf(
													IscsiService(vstorageId = testDisk.id)
											)
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageGvinumAllocation(
															hostId = testFreeBsdHost.id,
															actualSize = 10.GB,
															configuration = SimpleGvinumConfiguration(
																	diskName = "gvinum-disk-1"
															),
															capabilityId = testGvinumCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("single not shared") {
			val allocation = VirtualStorageGvinumAllocation(
					hostId = testFreeBsdHost.id,
					actualSize = 10.GB,
					configuration = SimpleGvinumConfiguration(
							diskName = "gvinum-disk-1"
					),
					capabilityId = testGvinumCapability.id
			)
			CtldIscsiShareFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testFreeBsdHost),
							hostDyns = listOf(
									HostDynamic(id = testFreeBsdHost.id, status = HostStatus.Up)
							),
							hostCfgs = listOf(
									HostConfiguration(
											id = testFreeBsdHost.id,
											services = listOf()
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													allocation
											)
									)
							)
					)
			) == listOf(CtldIscsiShare(vstorage = testDisk, allocation = allocation, host = testFreeBsdHost))
		}
	}
}