package com.github.kerubistan.kerub.planner.steps.storage.lvm.duplicate

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import com.github.kerubistan.kerub.testOtherHost
import io.github.kerubistan.kroki.size.GB

internal class DuplicateToLvmTest : OperationalStepVerifications() {
	override val step = DuplicateToLvm(
			sourceHost = testHost,
			targetHost = testOtherHost,
			target = VirtualStorageLvmAllocation(
					vgName = testLvmCapability.volumeGroupName,
					hostId = testOtherHost.id,
					capabilityId = testLvmCapability.id,
					actualSize = 1.GB,
					mirrors = 0,
					path = ""
			),
			source = VirtualStorageLvmAllocation(
					vgName = testLvmCapability.volumeGroupName,
					hostId = testHost.id,
					capabilityId = testLvmCapability.id,
					actualSize = 1.GB,
					mirrors = 0,
					path = ""
			),
			virtualStorageDevice = testDisk
	)
}