package com.github.kerubistan.kerub.planner.steps.vstorage.migrate.live.libvirt

import com.github.kerubistan.kerub.planner.OperationalState
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertTrue

@Ignore("TODO")
class LibvirtMigrateVirtualStorageDeviceFactoryTest {

	@Test
	fun produce() {
		assertTrue("blank state - no steps") {
			LibvirtMigrateVirtualStorageDeviceFactory.produce(OperationalState.fromLists()).isEmpty()
		}
	}
}