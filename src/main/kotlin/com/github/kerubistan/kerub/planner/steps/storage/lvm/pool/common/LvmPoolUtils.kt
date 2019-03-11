package com.github.kerubistan.kerub.planner.steps.storage.lvm.pool.common

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.StorageCapability

val percents = (1..9) + (10..90).step(10) + (91..98)

fun volumeGroupId(storageCapabilities : List<StorageCapability>, vgName : String) =
		storageCapabilities.first {
			it is LvmStorageCapability && it.volumeGroupName == vgName
		}.id
