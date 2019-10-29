package com.github.kerubistan.kerub.planner.steps.storage.gvinum.unallocate

import com.github.kerubistan.kerub.model.dynamic.VirtualStorageGvinumAllocation
import com.github.kerubistan.kerub.model.dynamic.gvinum.SimpleGvinumConfiguration
import com.github.kerubistan.kerub.planner.steps.OperationalStepVerifications
import com.github.kerubistan.kerub.testDisk
import com.github.kerubistan.kerub.testFreeBsdHost
import com.github.kerubistan.kerub.testGvinumCapability
import com.github.kerubistan.kerub.testHost
import io.github.kerubistan.kroki.size.GB
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID.randomUUID

internal class UnAllocateGvinumTest : OperationalStepVerifications() {
	override val step = UnAllocateGvinum(
			host = testFreeBsdHost,
			allocation = VirtualStorageGvinumAllocation(
					hostId = testFreeBsdHost.id,
					configuration = SimpleGvinumConfiguration(
							diskName = "test-disk"
					),
					capabilityId = testGvinumCapability.id,
					actualSize = 1.GB
			),
			vstorage = testDisk
	)


	@Test
	fun validations() {
		assertThrows<IllegalStateException>("only bsd") {
			UnAllocateGvinum(
					host = testHost,
					allocation = VirtualStorageGvinumAllocation(
							hostId = testHost.id,
							configuration = SimpleGvinumConfiguration(
									diskName = "test-disk"
							),
							capabilityId = testGvinumCapability.id,
							actualSize = 1.GB
					),
					vstorage = testDisk
			)
		}
		assertThrows<IllegalStateException>("mixed hosts") {
			UnAllocateGvinum(
					host = testFreeBsdHost.copy(id = randomUUID()),
					allocation = VirtualStorageGvinumAllocation(
							hostId = testFreeBsdHost.id,
							configuration = SimpleGvinumConfiguration(
									diskName = "test-disk"
							),
							capabilityId = testGvinumCapability.id,
							actualSize = 1.GB
					),
					vstorage = testDisk
			)
		}
		UnAllocateGvinum(
				host = testFreeBsdHost,
				allocation = VirtualStorageGvinumAllocation(
						hostId = testFreeBsdHost.id,
						configuration = SimpleGvinumConfiguration(
								diskName = "test-disk"
						),
						capabilityId = testGvinumCapability.id,
						actualSize = 1.GB
				),
				vstorage = testDisk
		)
	}
}