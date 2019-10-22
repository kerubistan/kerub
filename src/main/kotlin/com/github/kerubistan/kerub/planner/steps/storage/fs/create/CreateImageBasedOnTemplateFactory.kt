package com.github.kerubistan.kerub.planner.steps.storage.fs.create

import com.github.kerubistan.kerub.model.Expectation
import com.github.kerubistan.kerub.model.FsStorageCapability
import com.github.kerubistan.kerub.model.dynamic.VirtualStorageFsAllocation
import com.github.kerubistan.kerub.model.expectations.CloneOfStorageExpectation
import com.github.kerubistan.kerub.model.expectations.StorageAvailabilityExpectation
import com.github.kerubistan.kerub.planner.OperationalState
import com.github.kerubistan.kerub.planner.issues.problems.Problem
import com.github.kerubistan.kerub.planner.steps.AbstractOperationalStepFactory
import com.github.kerubistan.kerub.utils.junix.qemu.QemuImg
import io.github.kerubistan.kroki.collections.join
import io.github.kerubistan.kroki.size.MB
import java.math.BigInteger
import kotlin.reflect.KClass

object CreateImageBasedOnTemplateFactory : AbstractOperationalStepFactory<CreateImageBasedOnTemplate>() {

	override fun produce(state: OperationalState): List<CreateImageBasedOnTemplate> =
		state.index.runningHosts.filter { QemuImg.available(it.stat.capabilities) }
				.mapNotNull { hostData ->
					hostData.stat.capabilities?.storageCapabilities
							?.filterIsInstance<FsStorageCapability>()?.filter { capability ->
								val storageTechnologies = state.controllerConfig.storageTechnologies
								capability.mountPoint in storageTechnologies.fsPathEnabled
										&& capability.fsType in storageTechnologies.fsTypeEnabled
										// only if it has some tiny space
										&& (hostData.dynamic?.storageStatusById?.get(capability.id)?.freeCapacity ?: BigInteger.ZERO) > 1.MB
							}?.map { mount ->
								state.index.virtualStorageNotAllocated
										.mapNotNull {
											storageTobeAllocated ->
											// a not yet allocated storage that have a clone expectation
											// on a read-only virtual disk, that has an allocation on this host
											// in the supported formats
											storageTobeAllocated.index.expectationsByClass[CloneOfStorageExpectation::class.java.name]?.let {
												expectations ->
												val expectation = expectations.single() as CloneOfStorageExpectation
												val templateDisk = state.vStorage[expectation.sourceStorageId]
												templateDisk?.dynamic?.allocations?.filter {
													it is VirtualStorageFsAllocation
															&& it.hostId == hostData.id
															&& it.type in formatsWithBaseImage
												}?.map {
													templateAllocation ->
													CreateImageBasedOnTemplate(
															host = hostData.stat,
															baseDisk = templateDisk.stat,
															disk = storageTobeAllocated,
															capability = mount,
															format = templateAllocation.type,
															baseAllocation = templateAllocation as VirtualStorageFsAllocation
													)
												}
											}
										}
							}
				}.join().join().join()

	override val problemHints: Set<KClass<out Problem>>
		get() = setOf()

	override val expectationHints: Set<KClass<out Expectation>>
		get() = setOf(StorageAvailabilityExpectation::class)
}