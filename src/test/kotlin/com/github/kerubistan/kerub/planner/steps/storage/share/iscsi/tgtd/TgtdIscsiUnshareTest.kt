package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.tgtd

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB

internal class TgtdIscsiUnshareTest : OperationalStepVerifications() {
	override val step = TgtdIscsiUnshare(
			host = testHost,
			vstorage = testDisk,
			allocation = VirtualStorageLvmAllocation(
					hostId = testHost.id,
					path = "/dev/blah/${testDisk.id}",
					capabilityId = testLvmCapability.id,
					vgName = testLvmCapability.volumeGroupName,
					actualSize = 1.GB,
					mirrors = 0
			)
	)

}