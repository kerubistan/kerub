package com.github.kerubistan.kerub.planner.steps.storage.lvm.util

import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.dynamic.HostDynamic
import java.math.BigInteger

fun hasEnoughFreeCapacity(volGroup: LvmStorageCapability,
						  virtualStorageDevice: VirtualStorageDevice,
						  hostDynamic: HostDynamic?) =
		(volGroup.size > virtualStorageDevice.size
				&& actualFreeCapacity(hostDynamic, volGroup) > virtualStorageDevice.size)

fun actualFreeCapacity(hostDynamic: HostDynamic?, volGroup: LvmStorageCapability) =
		(hostDynamic?.storageStatus?.firstOrNull { it.id == volGroup.id }?.freeCapacity)
				?: BigInteger.ZERO

