package com.github.kerubistan.kerub.planner.steps.storage.lvm.vg

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.LvmStorageCapability
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.hosts.hardware.FailingStorageDevice
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import io.github.kerubistan.kroki.collections.join
import java.math.BigInteger
import kotlin.reflect.KClass

object RemoveDiskFromVGFactory : AbstractOperationalStepFactory<RemoveDiskFromVG>() {
	override fun produce(state: OperationalState) = state.hosts.values.map { host ->
		host.dynamic?.storageDeviceHealth?.mapNotNull { (device, status) ->
			if (!status) {
				host.stat.capabilities?.blockDevices?.firstOrNull { it.deviceName == device }?.let { blockDevice ->
					host.stat.capabilities.storageCapabilities.filterIsInstance<LvmStorageCapability>()
							.mapNotNull { capability ->
								val capFree =
										host.dynamic.storageStatusById[capability.id]?.freeCapacity ?: BigInteger.ZERO
								if (capability.storageDevices.size > 1 &&
										device in capability.storageDevices &&
										capFree > blockDevice.storageCapacity
								) {
									RemoveDiskFromVG(device = device, capability = capability, host = host.stat)
								} else null
							}
				}
			} else null
		} ?: listOf()
	}.join().join()

	override val problemHints = setOf(FailingStorageDevice::class)
	override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf()
}