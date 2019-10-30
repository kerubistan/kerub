package com.github.kerubistan.kerub.planner.steps.storage.share.iscsi.ctld

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageLvmAllocation
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testHost
import com.github.kerubistan.kerub.testLvmCapability
import io.github.kerubistan.kroki.size.GB

internal class CtldIscsiShareTest : OperationalStepVerifications() {
	override val step = CtldIscsiShare(
			host = testHost,
			allocation = VirtualStorageLvmAllocation(
					vgName = testLvmCapability.volumeGroupName,
					capabilityId = testLvmCapability.id,
					hostId = testHost.id,
					path = "/dev/blah/${testDisk.id}",
					actualSize = 1.GB
			),
			vstorage = testDisk
	)

}