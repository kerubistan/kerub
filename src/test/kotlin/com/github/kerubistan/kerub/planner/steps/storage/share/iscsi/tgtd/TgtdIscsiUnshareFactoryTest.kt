package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.SoftwarePackage
import com.github.kerubistan.kerub.model.Version.Companion.fromVersionString
import com.github.kerubistan.kerub.model.config.HostConfiguration
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import com.github.kerubistan.kerub.model.dynamic.HostStatus
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageDeviceDynamic
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.model.services.IscsiService
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.steps.AbstractFactoryVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testHostCapabilities
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import kotlin.test.assertTrue

class TgtdIscsiUnshareFactoryTest : AbstractFactoryVerifications(TgtdIscsiUnshareFactory) {

	@Test
	fun produce() {
		assertTrue("one host with lvm allocation - but not shared") {
			TgtdIscsiUnshareFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(testHost),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(
													VirtualStorageLvmAllocation(
															hostId = testHost.id,
															vgName = "test-vg",
															actualSize = 10.GB,
															path = "/dev/test-vg/blah",
															capabilityId = testLvmCapability.id
													)
											)
									)
							)
					)
			).isEmpty()
		}
		assertTrue("one host with lvm allocation shared, not used") {
			val host = testHost.copy(
					capabilities = testHostCapabilities.copy(
							distribution = SoftwarePackage(
									name = "CentOS Linux",
									version = fromVersionString("7.0")
							),
							installedSoftware = listOf()
					)
			)
			val allocation = VirtualStorageLvmAllocation(
					hostId = host.id,
					vgName = "test-vg",
					actualSize = 10.GB,
					path = "/dev/test-vg/blah",
					capabilityId = testLvmCapability.id
			)
			TgtdIscsiUnshareFactory.produce(
					OperationalState.fromLists(
							hosts = listOf(host),
							hostDyns = listOf(HostDynamic(id = testHost.id, status = HostStatus.Up)),
							hostCfgs = listOf(
									HostConfiguration(
											id = host.id,
											services = listOf(
													IscsiService(vstorageId = testDisk.id, password = "test-password")
											)
									)
							),
							vStorage = listOf(testDisk),
							vStorageDyns = listOf(
									VirtualStorageDeviceDynamic(
											id = testDisk.id,
											allocations = listOf(allocation)
									)
							)
					)
			) == listOf(TgtdIscsiUnshare(host = host, vstorage = testDisk, allocation = allocation))
		}

	}
}