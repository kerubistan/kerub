package com.github.kerubistan.kerub.planner.steps.vstorage.fs.truncate

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.VirtualStorageDevice
import com.github.kerubistan.kerub.model.collection.HostDataCollection
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.io.VirtualDiskFormat
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.vstorage.fs.base.AbstractCreateFileVirtualStorageFactory
import com.github.kerubistan.kerub.utils.junix.common.OsCommand
import com.github.kerubistan.kerub.utils.junix.truncate.Truncate
import java.math.BigInteger
import kotlin.reflect.KClass

object TruncateImageFactory : AbstractCreateFileVirtualStorageFactory<TruncateImage>() {

	override val problemHints = setOf<KClass<out Problem>>()
	override val expectationHints = setOf<KClass<out Expectation>>()

	override val requiredOsCommand: OsCommand
		get() = Truncate

	override fun createStep(
			storage: VirtualStorageDevice,
			hostData: HostDataCollection,
			mount: FsStorageCapability) = TruncateImage(
			host = hostData.stat,
			disk = storage,
			allocation = VirtualStorageFsAllocation(
					hostId = hostData.stat.id,
					actualSize = BigInteger.ZERO,
					fileName = "${storage.id}.raw",
					mountPoint = mount.mountPoint,
					type = VirtualDiskFormat.raw
			)
	)
}